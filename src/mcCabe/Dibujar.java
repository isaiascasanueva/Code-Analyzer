package mcCabe;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dibujar {
	public Nodo nodo = new Nodo("");

	public static void main(String a[]) {
		Nodo n = dibujar(
				"		codigo = codigo.replace(\"\\\\\\\"\", \"\").replaceAll(\"\\\"[^\\\"]*\\\"|'[^']*'\", \"\");\r\n"
						+ "		int lineascomentarios = 0;\r\n" + "		int lineascodigo = 0;\r\n"
						+ "		boolean comentarioMultilinea = false;\r\n"
						+ "		for (String linea : codigo.split(\"\\n\")) {\r\n"
						+ "			linea = linea.trim();\r\n" + "			if (comentarioMultilinea) {\r\n"
						+ "				lineascomentarios++;\r\n"
						+ "				comentarioMultilinea = !linea.contains(\"*/\");\r\n"
						+ "			} else if (linea.startsWith(\"/*\"))   {\r\n"
						+ "				comentarioMultilinea = true;\r\n" + "				lineascomentarios++;\r\n"
						+ "			} else if (!linea.startsWith(\"//\")) {\r\n"
						+ "				if (linea.length() > 2)\r\n" + "					lineascodigo++;\r\n"
						+ "				if (linea.contains(\"//\"))\r\n"
						+ "					lineascomentarios++;\r\n" + "			} else\r\n"
						+ "				lineascomentarios++;\r\n" + "		}\r\n"
						+ "		return new int[] { lineascomentarios, lineascodigo };");
		return;
	}

	static Pattern p = Pattern.compile("\\s(if|for|while|switch|do|else if)\\s*\\(([^\\n]+)\\)\\s*(?:\\n|\\{)");

	public static Nodo dibujar(String cod) {
		Matcher m = p.matcher(cod);
		if (m.find()) {
			Nodo n = new Nodo(cod.substring(0, m.start()));
			String pregunta = m.group(2), tipo = m.group(1), siguiente = cod.substring(m.end() - 1);
			n.add(new NodoCondicion(pregunta, tipo, siguiente));
			return n;
		} else
			return new Nodo(cod);
	}
}
