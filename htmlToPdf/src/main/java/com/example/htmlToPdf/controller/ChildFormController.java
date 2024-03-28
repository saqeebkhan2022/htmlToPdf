package com.example.htmlToPdf.controller;
import com.example.htmlToPdf.model.ChildForm;
import com.example.htmlToPdf.repository.ChildFormRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class ChildFormController {

    private final ChildFormRepository childFormRepository;

    public ChildFormController(ChildFormRepository childFormRepository) {
        this.childFormRepository = childFormRepository;
    }

    @PostMapping("/saveData")
    public ResponseEntity<String> saveData(@RequestBody ChildForm formData) {
        childFormRepository.save(formData);
        return ResponseEntity.ok("Data saved successfully");
    }

    @GetMapping("/generate-pdf")
    public ResponseEntity<byte[]> generatePdfFromHtml() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            String processedHtml = processHtml(htmlContent);
            renderer.setDocumentFromString(processedHtml);
            renderer.layout();
            renderer.createPDF(outputStream);
            byte[] pdfBytes = outputStream.toByteArray();
            pdfBytes = flattenFormFields(pdfBytes);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename("output.pdf").build());
            headers.setContentLength(pdfBytes.length);
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private String processHtml(String htmlContent) {
        // Regular expression to match unchecked checkboxes
        String regex = "<input[^>]type=\"checkbox\"[^>]>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(htmlContent);

        // Remove unchecked checkboxes from the HTML content
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String checkbox = matcher.group();
            if (checkbox.contains("checked")) {
                matcher.appendReplacement(buffer, checkbox);
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }


    private byte[] flattenFormFields(byte[] pdfBytes) throws IOException {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
            if (acroForm != null) {
                acroForm.flatten();
            }

            try (ByteArrayOutputStream flattenedStream = new ByteArrayOutputStream()) {
                document.save(flattenedStream);
                return flattenedStream.toByteArray();
            }
        }
    }
            String htmlContent = "<html>\n" +
                    "<head>\n" +
                    "<title>JFS-01234.PDF</title>\n" +
                    "</head>\n" +
                    "<body style=\"text-align: center; \">\n" +
                    "<p class=\"s1\" style=\"padding-top: 30pt;text-indent: 0pt;text-align: center;\">Ohio Department of\n" +
                    "        Job and Family Services</p>\n" +
                    "<h1 style=\"padding-top: 1pt;text-indent: 0pt;text-align: center;\"><span>CHILD ENROLLMENT AND\n" +
                    "        HEALTH INFORMATION FOR CHILD CARE</span></h1>\n" +
                    "\n" +
                    "<h2 style=\"padding-top: 11pt;text-indent: 0pt;text-align: center;font-size: 15px;\">This form shall be completed\n" +
                    "        prior to the child&#39;s first day of attendance and updated annually and as needed.</h2>\n" +
                    "<p style=\"text-indent: 0pt;\"></p>\n" +
                    "<table style=\"border-collapse:collapse; text-align: center; display: flex;\n" +
                    "        justify-content: center;\n" +
                    "        \" cellspacing=\"0\">\n" +
                    "<tr style=\"height:21pt\">\n" +
                    "\n" +
                    "<td style=\"border-top-style:solid;border-top-width:1pt;border-left-style:solid;border-left-width:1pt;border-bottom-style:solid;border-bottom-width:1pt;border-right-style:solid;border-right-width:1pt\"\n" +
                    "        colspan=\"4\">\n" +
                    "<input type=\"checkbox\" name=\"vehicle3\" value=\"Boat\" checked=\"checked\"></input>\n" +
                    "</td>\n" +
                    "<td style=\"border-top-style:solid;border-top-width:1pt;border-left-style:solid;border-left-width:1pt;border-bottom-style:solid;border-bottom-width:1pt;border-right-style:solid;border-right-width:1pt\"\n" +
                    "        colspan=\"4\">\n" +
                    "<input type=\"checkbox\" name=\"vehicle2\" value=\"aman\"></input>\n" +
                    "</td>\n" +

                    "</tr>\n" +
                    "\n" +
                    "</table>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>";


}

