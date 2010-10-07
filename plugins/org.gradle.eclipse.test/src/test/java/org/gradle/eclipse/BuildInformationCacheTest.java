package org.gradle.eclipse;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.gradle.foundation.ProjectView;

public class BuildInformationCacheTest {
	
	@Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

	BuildInformationCache cut; 
		    
	@Before public void setUp(){
		cut = new BuildInformationCache();
	}
    
	@Test
    public void testDifferentBuildFilesWithSameContentHaveSameMD5() throws IOException {
        File buildFile1 = testFolder.newFile("build1.gradle");
        File buildFile2 = testFolder.newFile("build2.gradle");
        
        writeToFile(buildFile1, "apply plugin:'java'");
        writeToFile(buildFile2, "apply plugin:'java'");
        
        String md5ForBuildFile1 = cut.calculateMd5StringForFile(buildFile1.getAbsolutePath());
        String md5ForBuildFile2 = cut.calculateMd5StringForFile(buildFile2.getAbsolutePath());

        assertEquals(md5ForBuildFile1, md5ForBuildFile2);
	}
	
	@Test
    public void testBuildFilesWithDifferentContentHaveDifferentMD5() throws IOException {
        File buildFile1 = testFolder.newFile("build1.gradle");
        File buildFile2 = testFolder.newFile("build2.gradle");
        
        writeToFile(buildFile1, "apply plugin:'java'");
        writeToFile(buildFile2, "apply plugin:'groovy'");
        
        String md5ForBuildFile1 = cut.calculateMd5StringForFile(buildFile1.getAbsolutePath());
        String md5ForBuildFile2 = cut.calculateMd5StringForFile(buildFile2.getAbsolutePath());

        assertFalse(md5ForBuildFile1.equals(md5ForBuildFile2));
	}
	
	@Test public void testProjectListReferencedByFilePathReturnsAlwaysSameListObject() throws IOException{
		File buildFile1 = testFolder.newFile("build1.gradle");
	    writeToFile(buildFile1, "apply plugin:'java'");

	    List<ProjectView> projectList = new ArrayList<ProjectView>();
		
		cut.put(buildFile1.getAbsolutePath(), projectList);
		
		assertSame(cut.get(buildFile1.getAbsolutePath()), cut.get(buildFile1.getAbsolutePath()));
	}
	
	@Test public void testChangedFileContentInvalidatesCacheForFile() throws IOException{
		File buildFile1 = testFolder.newFile("build1.gradle");
	    writeToFile(buildFile1, "apply plugin:'java'");

	    List<ProjectView> projectList = new ArrayList<ProjectView>();
		
		cut.put(buildFile1.getAbsolutePath(), projectList);
		
		writeToFile(buildFile1, "apply plugin:'groovy'");
		assertNull(cut.get(buildFile1.getAbsolutePath()));
	}
    
    /** Write fixed content to the given file. */
    private void writeToFile(File file, String content) throws IOException  {
      Writer out = new OutputStreamWriter(new FileOutputStream(file));
      try {
        out.write(content);
      }
      finally {
        out.close();
      }
    }
}
