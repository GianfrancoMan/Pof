package manca.pof.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import manca.pof.model.PathOption;

class FileManager {

  private static FileManager fileManager;

  private FileManager() {}

  public static FileManager getInstance() {
    if (fileManager == null)
      return new FileManager();
    else
      return fileManager; 
  }






  /**
   * Reads the file from the first  to the last line and returns it as a List<String> type.
   * @param path The file path.
   * @return The lines of the file read in the form of a list of strings.
   */
  public  List<String> read(Path path) {
    try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
      String line="";
      List<String> list = new ArrayList<>();
      while((line = br.readLine()) != null) {
        list.add(line);
      }
      return list;
      
    } catch (Exception e) {
      System.out.println("Exception: " + e.getLocalizedMessage());
      return new ArrayList<String>();
    }
  }





  /**
   * 
   * @param sequence
   * @param pathOption
   * @return
   */
  public boolean appendByOption(String sequence, PathOption pathOption) {
    Path path = Paths.get(pathOption.value());
    try {
      Files.write(path, sequence.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.APPEND);
    } catch (IOException e) {
      return false;
    }
    return true;
  }




  /**
   * Replace the content of the csv file which refers pathOption parameter with the content of the list parameter.
   * @param option the option that refers to a csv file.
   * @param list the new content for the csv file.
   * @return true if the operation is successful.
   */
  public boolean replaceCSVByOption(PathOption option, List<String> list) {
    Path path = Paths.get(option.value());
    try {
      Files.write(path, list, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      return false;
    }
    return true;
  }





  /**
   * Changes the availability of a product from true to false and vice versa
   * @param idProduct the unique ID of the product
   * @return true if operation is successful otherwise false.
   */
  public boolean toggleProductAvailability(int idProduct) {
    try (RandomAccessFile raf = new RandomAccessFile(PathOption.PRODUCTS.value(), "rw")) {
      String l;
      var pos = raf.getFilePointer();
      while ( (l = raf.readLine()) != null) {
        String[] parts = l.split(";");
        if(parts[0].equals(Integer.toString(idProduct))) {
          parts[5] = parts[5].trim().equals("true") ? "false" : "true ";
          String toggledString = String.join(";", parts);
          raf.seek(pos);
          raf.writeBytes(toggledString);
          break;
        }
        pos = raf.getFilePointer();
      }
    } catch (IOException e){
      return false;
    }
    return true;
  } 



  /**
   * Create a file in the location which is specified by the 'filePath' parameter and
   * writes the 'lines' passed as parameter.
   * @param filePath the path of the file to create including the file name and its extension
   * @param lines the lines of string that will be written
   * @return
   */
  public boolean writeFile(String filePath, List<String> lines) {
    Path path = Paths.get(filePath);
    try {
      Files.createFile(path);
      Files.write(path, lines, StandardOpenOption.WRITE);
      return true;

    } catch (IOException e) {
        return false;
    }
  }
}
