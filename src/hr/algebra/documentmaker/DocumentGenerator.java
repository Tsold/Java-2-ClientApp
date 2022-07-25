package hr.algebra.documentmaker;

import hr.algebra.controllers.AdminAddAdsController;
import hr.algebra.controllers.LoginController;
import hr.algebra.model.Vehicle;
import hr.algebra.repository.ISqlRepo;
import hr.algebra.sql.SqlRepo;
import hr.algebra.utils.FileUtils;
import hr.algebra.utils.MessageUtils;
import hr.algebra.utils.ReflectionUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocumentGenerator {

    private static final String DOCUMENTATION_FILENAME = "documentation.html";


    public static void docMaker() {
        generatingDoc(fillPaths());
    }


    private static List<String> fillPaths() {
        List<String> PATHS = new ArrayList<>();
        LoginController log = new LoginController();
        DocumentGenerator doc = new DocumentGenerator();
        Vehicle veh = new Vehicle();
        ISqlRepo repo = new SqlRepo();
        FileUtils fileUtils = new FileUtils();
        PATHS.add(log.getClass().getPackage().getName());
        PATHS.add(doc.getClass().getPackage().getName());
        PATHS.add(veh.getClass().getPackage().getName());
        PATHS.add(repo.getClass().getPackage().getName());
        PATHS.add(fileUtils.getClass().getPackage().getName());
        return PATHS;
    }


    private static void generatingDoc(List<String> paths ){

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(DOCUMENTATION_FILENAME))) {


            StringBuilder docWriter = new StringBuilder();

            docWriter.append("<!DOCTYPE html>");
            docWriter.append("\n");
            docWriter.append("<html lang=\"en\">");
            docWriter.append("\n");
            docWriter.append("<head>");
            docWriter.append("\n");
            docWriter.append("<link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css\" integrity=\"sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T\" crossorigin=\"anonymous\">");
            docWriter.append("\n");
            docWriter.append("<title>Class documentation</title>");
            docWriter.append("\n");
            docWriter.append("<style>\n" +
                    "p{\n" +
                    "margin-bottom:-5px;\n" +
                    "\n" +
                    "}\n" +
                    "h3{\n" +
                    "margin-top:10px;\n" +
                    "\n" +
                    "}\n" +
                    ".border{\n" +
                    "border: 5px solid rgba(255, 255, 255, 0.3);\n" +
                    "\n" +
                    "}\n" +
                    ".jumbotron{\n" +
                    "margin-top:10px;\n" +
                    "\n" +
                    "}\n" +
                    "</style>");
            docWriter.append("\n");
            docWriter.append("</head>");
            docWriter.append("\n");
            docWriter.append("<body>");
            docWriter.append("<div class=\"jumbotron text-center\" style=\"margin-bottom:10px; margin-top:-10px\">\n" +
                    "  <h1>Code Documentation</h1>\n" +
                    "</div>");
            docWriter.append("<div class=\"container text-left border\">");

            paths.forEach(classPath -> {
                String dirClassPath = "src/" + classPath.replace(".","/");
                DirectoryStream<Path> stream = null;
                try {
                    stream = Files.newDirectoryStream(Paths.get(dirClassPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String CLASSES_PACKAGE = classPath + ".";
            stream.forEach(file -> {
                String filename = file.getFileName().toString();
                String className = filename.substring(0, filename.indexOf("."));
                docWriter.append("<div class=\"jumbotron text-center\">");
                docWriter.append("<h1>");
                docWriter
                        .append(className)
                        .append("\n");
                        docWriter.append("</h1>");
                        docWriter.append("</div>");

                try {
                    Class<?> clazz = Class.forName(CLASSES_PACKAGE.concat(className));
                    ReflectionUtils.readClassAndMembersInfo(clazz, docWriter);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(AdminAddAdsController.class.getName()).log(Level.SEVERE, null, ex);
                }
                docWriter.append("<p>");
                docWriter
                        .append("\n")
                        .append("\n");
                docWriter.append("</p>");

            });
            });
            docWriter.append("\n");
            docWriter.append("</div>");
            docWriter.append("\n");
            docWriter.append("</body>");
            docWriter.append("\n");
            docWriter.append("</html>");
            writer.write(docWriter.toString());

            MessageUtils.showInfoMessage("Reflection", "Documentation created", "Check '" + DOCUMENTATION_FILENAME + "'");
        } catch (IOException ex) {
            Logger.getLogger(AdminAddAdsController.class.getName()).log(Level.SEVERE, null, ex);
        }


    }



}
