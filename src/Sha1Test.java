import java.io.*;
import java.net.*;
import freenet.support.HexUtil;
import gnu.crypto.hash.*;
import com.izforge.izpack.util.AbstractUIProcessHandler;

public class Sha1Test {
	protected static int BUFFERSIZE=8192;
	private static final String base = new String("http://downloads.freenetproject.org/alpha/");

	public static void run(AbstractUIProcessHandler handler, String[] args){
		main(args);
	}

	public static void main(String[] args) {
		final String URI2 = args[0];
		final String path = args[1]+"/";
		int count = 0;
		if(URI2 == null) System.exit(2);

		String filename = (new File(URI2)).getName();
		System.out.println("Fetching "+filename);

		while(count<10){
			if(count>0)
				System.out.println("Attempt "+count);
			try{
				get(URI2+".sha1", path+filename+".sha1");
				if(sha1test(path+filename)) System.exit(0);
				get(URI2, path+filename);
			}catch(FileNotFoundException e){
				System.out.println("Not found, ignoring");
			}
			count++;
			try{
				Thread.sleep(5000);
			}catch(Exception e){
			}
		}
		System.out.println("No mirror is available at the moment, please try again later");
		System.exit(1);
	}

	public static boolean sha1test(String file) {
		Sha160 hash=new Sha160();
		try{
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			String result=new String();

			// We compute the hash
			// http://java.sun.com/developer/TechTips/1998/tt0915.html#tip2
			try {
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				int len = 0;
				byte[] buffer = new byte[BUFFERSIZE];
				while ((len = bis.read(buffer)) > -1) {
					hash.update(buffer,0,len);
				}
			} finally {
				if (bis != null) bis.close();
				if (fis != null) fis.close();
			}

			// We read the hash-file
			try {
				fis = new FileInputStream(file+".sha1");
				bis = new BufferedInputStream(fis);
				int len = 0;
				byte[] buffer = new byte[BUFFERSIZE];
				while ((len = bis.read(buffer)) > -1) {
					result+=new String(buffer,0,len);
				}
			} finally {
				if (bis != null) bis.close();
				if (fis != null) fis.close();
			}


			// now we compare
			byte[] digest=new byte[160];
			digest=hash.digest();

			int i=result.indexOf(' ');
			result=result.substring(0,i);
			if(result.equalsIgnoreCase(HexUtil.bytesToHex(digest))){
				return true;
			}else {
				return false;
			}

		}catch (Exception e){
			return false;
		}
	}


	public static void get(String file, String filename) throws FileNotFoundException{
		URL url;
		InputStream is = null;
		DataInputStream dis;
		String s;

		try {
			url = new URL(base+file);
			is = url.openStream();         // throws an IOException
			dis = new DataInputStream(new BufferedInputStream(is));
			File f = new File(filename);
			BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(f));
			int b;
			while ((b = dis.read()) != -1) {
				os.write(b);
			}
			os.close();

		} catch (MalformedURLException mue) {

			System.out.println("Ouch - a MalformedURLException happened.");
			mue.printStackTrace();
			System.exit(2);

		} catch (Exception ioe) {
			System.out.println(ioe);
		} finally {
			try {
				if(is != null) is.close();
			} catch (IOException ioe) {
			}
		}
	}
}