package org.jcvi.jillion.sam;

import org.jcvi.jillion.internal.sam.SamUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class TestSamNameExtension {

    @Parameterized.Parameters
    public static List<Object[]> data(){
        return List.of(
                new Object[]{"foo.sam", true},
                new Object[]{"foo.bam", false},
                new Object[]{"foo.sam.gz", true},
                new Object[]{"foo.sam.xz", true},
                new Object[]{"foo.txt", false},
                new Object[]{"foo.sam.txt", true}

                );
    }
    private String fileName;
    private boolean shouldPass;

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();


    public TestSamNameExtension(String fileName, boolean shouldPass){
        this.fileName = fileName;
        this.shouldPass = shouldPass;
    }

    @Test
    public void fileDoesNotExist() throws IOException {
        test(false);

    }
    @Test
    public void fileExists() throws IOException {
        test(true);

    }

    private void test(boolean createFile) throws IOException {
        File f;
        if(createFile){
            f = tmpDir.newFile(fileName);
        }else{
            f = new File(fileName);
        }

        if(shouldPass){
            SamUtil.assertHasSamFileExtension(f);
        }else{
            assertThrows(IllegalArgumentException.class, ()-> SamUtil.assertHasSamFileExtension(f));

        }
    }
}
