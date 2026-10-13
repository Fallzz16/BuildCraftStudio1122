import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/** Verifies the save-stream lifecycle that was broken by SyncFailedException. */
public final class SafeSaveStreamTest {
    public static void main(String[] args) throws Exception {
        byte[] data="buildcraft-save-roundtrip".getBytes("UTF-8");
        ByteArrayOutputStream buffer=new ByteArrayOutputStream();
        try(GZIPOutputStream zip=new GZIPOutputStream(buffer)) {zip.write(data);}
        File dir=Files.createTempDirectory("buildcraft-save-test").toFile();
        File tmp=new File(dir,"project.tmp");
        try(FileOutputStream out=new FileOutputStream(tmp)) {
            out.write(buffer.toByteArray());
            out.flush();
            out.getFD().sync();
        }
        byte[] decoded=new byte[128];
        int bytes;
        try(GZIPInputStream in=new GZIPInputStream(new FileInputStream(tmp))) {bytes=in.read(decoded);}
        if(bytes!=data.length)throw new AssertionError("Invalid decompressed size");
        for(int i=0;i<bytes;i++)if(data[i]!=decoded[i])throw new AssertionError("Invalid payload");
        if(!tmp.delete() || !dir.delete())throw new AssertionError("Clean-up failed");
        System.out.println("PASS: safe compressed save stream and fsync roundtrip");
    }
}
