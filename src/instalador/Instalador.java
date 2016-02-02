package instalador;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Instalador {
	
	
    private static final String configDir = "inst_test";
    private static final String configFile = "config.txt";
    private static final String libNameRoot = "DriversNativoC";
    
    
	public static boolean installDLLs() {
		
		String path = Config.getInstallService().getInstallPath();
		if (!path.endsWith(File.separator)) {
		    path += File.separator;
		}
		String libName = System.mapLibraryName(libNameRoot);
		path += libName;


		File extPath = new File(path);
		if (extPath.exists()) {
		    extPath.delete();
		} else {
		    extPath.getParentFile().mkdirs();
		}
		
		// Write the native library
		System.out.println("Recupera "+path);
		URL u = Instalador.class.getResource(libName);
		InputStream is = null;
		OutputStream os = null;
		byte buf[] = new byte[255];
		try {
		    is = u.openStream();
		    os = new FileOutputStream(extPath);
		    int len = 0;
		    while ((len=is.read(buf)) > 0) {
			os.write(buf, 0, len);
		    }
		} catch (IOException ioe) {
		    System.err.println("I/O Exception: " + ioe);
		    ioe.printStackTrace();
		    return false;
		} finally {
		    try {if (is!=null) is.close();} catch (IOException ioe) {}
		    try {if (os!=null) os.close();} catch (IOException ioe) {}
		}
		
		Properties p = new Properties();
		p.setProperty("libPath", extPath.toString());
		String home = System.getProperty("user.home");
		if (!home.endsWith(File.separator)) {
		    home += File.separator;
		}
		File propsFile = new File(home + configDir + File.separator + configFile);
		if (propsFile.exists()) {
		    propsFile.delete();
		} else {
		    propsFile.getParentFile().mkdirs();
		}
		OutputStream os2 = null;
		try {
		    os2 = new FileOutputStream(propsFile);
		    p.store(os2, "Installer test");
		} catch (IOException ioe) {
		    System.err.println("I/O Exception: " + ioe);
		    ioe.printStackTrace();
		    return false;
		} finally {
		    try {if (os2!=null) os2.close();} catch (IOException ioe) {}
		}
		Config.getInstallService().setNativeLibraryInfo(extPath.getParentFile().toString());
		return true;
		
	    }
	
	/** Do the installation of the files */
	public static void installJARS() throws Exception {
		
		String extDir = "\\lib\\ext\\";

		// Get a URL pointing to the installable JRE
		URL jreURL = null;
		try {
			jreURL = new URL("http://java.sun.com/products/autodl/j2se");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Get installation location of latest JRE
		String jrePath = Config.getInstallService().getInstalledJRE(jreURL,"1.7.0_67");

		// Get path to JRE/lib/ext
		File javaFile = new File( jrePath );//where java is located
		String jreRoot = javaFile.getParentFile().getParent();
		File libDir = new File( new File(jreRoot), extDir );
		if (!libDir.exists())
			libDir.mkdirs();
		
		// Get dir to install config stuff into
		File userDir = new File( new File( System.getProperty("user.home") ),".startDesk" );
		if( !userDir.exists() )
			userDir.mkdirs();

		// The obvious thing to do here would be to get this list from
		// a file in the install directory.  However we can't get
		// all the locations without being in this context, so we'd
		// have to at least have a limited set of possible destination
		// directories and have a pseudo name reference.
		Object files[] = new Object[] {
			new Object[] { "JDAI-TPV-LibExt.jar", libDir },
			new Object[] { "toolsIsban.jar", libDir },
			new Object[] { "JXFSclient.jar", libDir }			//puede seguir con más 
		};

		for( int i = 0; i < files.length; ++i ) {
			Object[] info = (Object[])files[i];
			String file = (String)info[0];
			File dir = (File)info[1];
			File instFile = new File( dir, file );
			Config.getInstallService().setStatus( "Installing: "+file+" in "+instFile );

			FileOutputStream fos = new FileOutputStream(instFile);
			try {
				URL fileURL = new URL(Config.getBasicService().getCodeBase(), file );
				URLConnection fileConn = fileURL.openConnection();
				int len = fileConn.getContentLength();
				int div = len / 100;
				InputStream is = fileConn.getInputStream();
				byte data[] = new byte[div];
				try {
					int q,tot=0;
					while ((q = is.read(data,0,data.length)) != -1) {
						fos.write(data,0,q);
						tot+=q;
						Config.getInstallService().updateProgress( (tot * 100 ) / len );
					}
				} finally {
					is.close();
				}
			} finally {
				fos.close();
			}
		}
		
		Config.getInstallService().installSucceeded(false);
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame frame = new JFrame();

		if (args.length > 0 && args[0].equals("install")) {
			System.out.println("Instalando DLLs...");
			
			//boolean success = installDLLs();
			try {
				installJARS();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			
			System.out.println("Fin de  Extension Instalacion de DLLs.");
//			if (success) {
//				Config.getInstallService().installSucceeded(false);
//			} else {
//				Config.getInstallService().installFailed();
//			}
		} else {
			System.out.println("Instalador llamado sin argumento install");
			try {
				JOptionPane.showMessageDialog(frame,
						"Instalador invocado: " + "se encarga de copiar a local los recursos necesarios");

			} finally {
				System.exit(0);
			}
		}
		System.exit(0);

	}

}