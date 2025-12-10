package test;

import org.example.general.ApplicationContext;
import org.example.menu.StorageDAO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class StorageTest {

    private final StorageDAO storageDAO = ApplicationContext.getStorageDAO();

    public void run() throws FileNotFoundException {

        File testImg = new File("C:/Users/John/Desktop/히히 못가.jpg");
        FileInputStream fis = new FileInputStream(testImg);
        storageDAO.insert(1, fis, testImg.length());
    }
}
