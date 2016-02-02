package proyectoJava;

import java.io.IOException;
import java.io.InputStream;

public class Programa{

	// los dos métodos nativos en la dll
	private native String getString();
	private native int addAtoB(int a, int b);

	private static boolean _ready = true;

	Programa() {

		try {
			System.out.println("Programa que invoca Instalar Drivers");
			String os = System.getProperty("os.name");
			System.out.println("Loading " + os + " native libraries...");

			if (os.startsWith("Windows")) {
				System.out.print("  DriversNativoC.dll... ");
				try {
					System.loadLibrary("DriversNativoC"); // drop ".dll"
					System.out.println("OK");
				} catch (UnsatisfiedLinkError ule) {
					System.out.println("Load Failed");
					_ready = false;
					ule.printStackTrace();
				}

			} else if (os.startsWith("Linux")) {
				System.loadLibrary("DriversNativoC"); // drop "lib" prefix & ".so"

			} else if (os.startsWith("Mac")) {
				System.loadLibrary("DriversNativoC"); // drop ".jnilib"

			} else {
				System.out.println("Sorry, OS '" + os + "' no soportado.");
				System.exit(1);
			}
			// invoco metodos de la dll
			String s = "No funciona";
			if (_ready) {
				try {
					s = getString();
					s += " ->" + Integer.toString(addAtoB(2, 3));
					System.out.println(s);

				} catch (UnsatisfiedLinkError ule) {
					ule.printStackTrace();
				}
			}

			// Ejecuto la arquitectura cliente después de la fase de instalación....
			//con esto , salta el programa asociado a *.txt (el notepad probablemente)
			String[] command = {"cmd.exe","/c","C:/DATOS/D/DOCS/testArqClienteArrancada.txt"};
			Process p = Runtime.getRuntime().exec(command);
			//esto también funciona
			//Process p = Runtime.getRuntime().exec("notepad.exe C:/DATOS/D/DOCS/testArqClienteArrancada.txt");
			
			p.waitFor();

			if (p.exitValue() != 0)
				System.out.println("** Error en el arranque de arquitectura cliente");

			else {
				InputStream is = p.getInputStream();
				int i = 0;
				while ((i = is.read()) != -1) {
					System.out.print((char) i);
				}
				System.out.println("Arquitectura cliente arrancada");
			}
		} catch (IOException ex) {
			// Validate the case the file can't be accesed (not enought
			// permissions)
			_ready = false;

		} catch (InterruptedException ex) {
			// Validate the case the process is being stopped by some external
			// situation
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Programa();
	}

}
