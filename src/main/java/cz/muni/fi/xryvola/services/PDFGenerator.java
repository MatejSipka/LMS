package cz.muni.fi.xryvola.services;

import cz.muni.fi.xryvola.MyVaadinUI;

import java.io.*;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by adam on 11.1.15.
 */
public class PDFGenerator {

    public void generatePresentation(Presentation presentation){

        File file = new File("/home/adam/Plocha/BPGenerator");
        if (!file.exists()) file.mkdir();
        File generatedXHTML = new File (MyVaadinUI.MYFILEPATH + presentation.getId() + ".html");
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(generatedXHTML), "UTF-8"));

            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" +
                    "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
            out.write("<html>");
            out.write("<head>" +
                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\" />"  +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /> </head>");
            out.write("<body>");
            /**WRITE SLIDES CONTENTS**/

            for (Slide slide : presentation.getSlides()){
                out.write("<div class=\"slide\">");
                out.write(slide.getHtmlContent());
                out.write("</div>");
                out.write("<div style=\"page-break-after: always\"><span style=\"display:none\">&nbsp;</span></div>");
            }
            out.write("</body></html>");
            out.flush();
            out.close();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        /*PDF GEN*/
        String cmd = "wkhtmltopdf --margin-right 0 --margin-top 0 --margin-left 0 --margin-bottom 0 --page-height 130 --page-width 166 " + MyVaadinUI.MYFILEPATH + presentation.getId() + ".html " +  MyVaadinUI.MYFILEPATH + presentation.getId() + ".pdf";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void generateTest(Test test, Boolean withResult, String name){
        String path = MyVaadinUI.MYFILEPATH;
        File file = new File("/home/adam/Plocha/BPGenerator");
        if (!file.exists()) file.mkdir();
        File generatedXHTML = new File (file.getAbsolutePath() + File.separator + name + ".html");
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(generatedXHTML), "UTF-8"));
            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" +
                    "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
            out.write("<html>");
            out.write("\t<head>\n" +
                    "\t\t<meta charset=\"UTF-8\">\n" +
                    "\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"style/style.css\">\n" +
                    "\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"style/fontAwsome/font-awesome.min.css\">\n" +
                    "\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"style/fontAwsome/font-awesome.css\">\n" +
                    "\t\t<link href='http://fonts.googleapis.com/css?family=Play&subset=latin,latin-ext' rel='stylesheet' type='text/css'>\n" +
                    "\t</head>\n" +
                    "\t<body>");
            out.write("<img src=\"style/images/logo_zshavl.jpg\" width=\"100px\" align=\"right\"><h1>");
            out.write(test.getName());
            out.write("</h1>");
            for (Question question : test.getQuestions()){
                out.write("<div class=\"question\">");
                out.write("<p>" + question.getQuestion() + "</p>");
                out.write("<ul>");
                for (Answer answer : question.getAnswers()){
                    if (withResult && answer.getIsCorrect()){
                        out.write("<li class=\"correct\">");
                    }else {
                        out.write("<li>");
                    }
                    out.write(answer.getAnswer());
                    out.write("</li>");
                }
                out.write("</ul>");
                out.write("</div>");
            }
            out.write("</body></html>");
            out.flush();
            out.close();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*PDF GEN*/
        String cmd = "wkhtmltopdf " + MyVaadinUI.MYFILEPATH + name + ".html " + " " + MyVaadinUI.MYFILEPATH + name + ".pdf";
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void generateTests(Test test, int number){

        try {
            FileOutputStream fos = new FileOutputStream(MyVaadinUI.MYFILEPATH + test.getId() + ".zip");
            ZipOutputStream zos = new ZipOutputStream(fos);
            for(int i = 1; i <= number; i++){
                String name = test.getId() + "-" + i;
                Collections.shuffle(test.getQuestions());
                generateTest(test, false, name);
                String resname = name + "res";
                generateTest(test, true, resname);
            }

            for (int i = 1; i <= number; i++){
                String name = test.getId() + "-" + i;
                String resname = name + "res";
                addToZipFile(name+".pdf", zos);
                addToZipFile(resname+".pdf", zos);
            }
            zos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void addToZipFile(String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {

        System.out.println("Writing '" + fileName + "' to zip file");

        File file = new File(MyVaadinUI.MYFILEPATH + fileName);
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zos.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }

        zos.closeEntry();
        fis.close();
    }

}
