package BackEnd;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Clase {
	public String nombre;
	public String codigo;
	public ArrayList<Metodo> metodo = new ArrayList<>();
	private boolean modoAvanzado = false;
	public int cc;
	public int nivelAlerta;
	public int lineasComentadas;
	public int lineasCodido;
	// private String contiene = "";

	public Clase(String clase, String full, String cod) {
		nombre = clase;
		extraerCodigoDeClase(full, cod);
		new Thread() {

			public void run() {
				int[] lineas = Evaluar.contarComentarios(codigo);
				lineasComentadas = lineas[0];
				lineasCodido = lineas[1];
				Evaluar.setCodigos(codigo);
			}
		}.start();
		sacarClasesQueEstenDentroDeLaClase();
		getFuncionesDeClase(nombre, codigo);
	}

	private void sacarClasesQueEstenDentroDeLaClase() {
		int indexOf = codigo.indexOf("{");
		Matcher match = Pattern.compile("class\\s+\\w+[ ,_]*(?:extends\\s+\\w+)?\\s*(?:implements[\\s\\w,]+)?")
				.matcher(codigo.substring(indexOf));
		if (match.find()) {
			String cod = codigo;
			int fin = match.start() + indexOf;
			int inicio = fin;
			cod = cod.substring(fin);
			int index = cod.indexOf("{") + 1;
			fin += index;
			cod = cod.substring(index);
			int nivelini = nivel(cod) - 1;
			while (nivelini != nivel(cod)) {
				index = cod.indexOf("}") + 1;
				fin += index;
				cod = cod.substring(index);
			}
			String claseMetida = codigo.substring(inicio, fin);
			codigo = codigo.replace(claseMetida, "");
		}

	}

	private void extraerCodigoDeClase(String full, String cod) {
		String codigo = cod;
		int fin = cod.indexOf(full);
		int inicio = fin;
		cod = cod.substring(fin);
		int index = cod.indexOf("{") + 1;
		fin += index;
		modoAvanzado = nivel(cod) != 0;
		cod = cod.substring(index);
		int nivelini = nivel(cod) - 1;
		while (nivelini != nivel(cod)) {
			index = cod.indexOf("}") + 1;
			fin += index;
			cod = cod.substring(index);
		}
		this.codigo = codigo.substring(inicio, fin).trim();
	}

	private int nivel(String cod) {
		if (modoAvanzado)
			cod = cod.replaceAll("(?:\\(|,\\s*)\".*\"(?:\\)|,)", "()");
		int largo = cod.length();
		int abiertas = largo - cod.replace("{", "").length();
		int cerradas = largo - cod.replace("}", "").length();
		int nivel = cerradas - abiertas;
		return nivel;
	}

	private void getFuncionesDeClase(String clase, String cod) {
		Pattern pat = Pattern.compile(Variables.funciones);
		Matcher match = pat.matcher(cod);
		while (match.find()) {
			String group0 = match.group(0).trim();
			if (!group0.startsWith("new")) {
				Metodo funcion = new Metodo(match.group(1), group0, cod, this);
				cod = cod.replace(funcion.codigoCompleto, "");
				metodo.add(funcion);
				// contiene += funcion;
			} else {
				int end = match.end();
				String substring = group0.substring(group0.indexOf("{"));
				Matcher match2 = Pattern.compile(Variables.funciones).matcher(substring);
				if (match2.find()) {
					Metodo funcion = new Metodo(match.group(1), substring, cod, this);
					cod = cod.replace(funcion.codigoCompleto, "");
					metodo.add(funcion);
					// contiene += funcion;
				} else
					cod = cod.substring(end);
			}

			match = pat.matcher(cod);
		}
	}

	@Override
	public String toString() {
		return nombre;
	}

	public int fan_inClase(Metodo metodo) {
		int fain = 0;
		Matcher match = Pattern.compile("[^\\w\\d_]" + metodo.nombre + "\\s*\\(").matcher(codigo);
		while (match.find())
			fain++;
		return fain;
	}
}