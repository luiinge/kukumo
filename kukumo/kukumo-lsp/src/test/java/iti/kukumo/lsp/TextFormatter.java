package iti.kukumo.lsp;

import iti.kukumo.lsp.internal.*;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TextFormatter {


	@Test
	public void testFormatter() {
		String document =
			  "         #language: es\n"
			+ "\n"
			+ "\n"
			+ "    @implementation\n"
			+ "  Característica:         Operaciones con usuarios          \n"
			+ "=======================================\n"
			+ " This is a description line\n"
			+ "\n"
			+ "Antecedentes:      \n"
			+ "          Dada la URL de conexión a BBDD 'jdbc:h2:tcp://localhost:9092/~/test' usando el usuario 'sa' y la contraseña ''\n"
			+ "      Y la URL base http://localhost:9191\n"
			+ "  Y el servicio REST '/users' \n"
			+ "\n"
			+ "      # redefinition.stepMap: 2-1-2\n"
			+ " @ID-1\n"
			+ "Escenario: Se consulta un usuario existente \n"
			+ "-----------------------------\n"
			+ "Dado un usuario identificado por '3' \n"
			+ "    Y que se ha insertado los siguientes datos en la tabla de BBDD USER:\n"
			+ "      | ID | FIRST_NAME | LAST_NAME |\n"
			+ "      | 3  | Pepe       | Perez     |\n"
			+ "      | 4  | Pepe       | Perez Martinez Bordiu     |\n"
			+ "  Cuando se consulta el usuario\n"
			+ "    Entonces el código de respuesta HTTP es 200\n"
			+ "  Y la respuesta es parcialmente:\n"
			+ "      ```json\n"
			+ "      { \n"
			+ "             \"firstName\": \"Pepe\" \n"
			+ "}\n"
			+ "```\n"
			+ "     \n"
			+ " @ID-2\n"
			+ "          Escenario: Se consulta un usuario existente \n"
			+ "Dado un usuario identificado por '3' \n"
			+ "    Y que se ha insertado los siguientes datos en la tabla de BBDD USER:\n"
			+ "      | ID | FIRST_NAME | LAST_NAME |\n"
			+ "      | 4  | Pepe| Perez Martinez Bordiu     |\n"
			+ "      | 3| Pepe  | Perez     |\n"
			+ "  Cuando se consulta el usuario\n"
			+ "";

		GherkinDocumentMap documentMap = new GherkinDocumentMap(document);
		GherkinFormatter.format(documentMap);
		assertThat(documentMap.rawContent()).isEqualTo(
				  "#language: es\n"
				+ "\n"
				+ "\n"
				+ "@implementation\n"
				+ "Característica: Operaciones con usuarios\n"
				+ "=======================================\n"
				+ "This is a description line\n"
				+ "\n"
				+ "    Antecedentes:\n"
				+ "        Dada la URL de conexión a BBDD 'jdbc:h2:tcp://localhost:9092/~/test' usando el usuario 'sa' y la contraseña ''\n"
				+ "        Y la URL base http://localhost:9191\n"
				+ "        Y el servicio REST '/users'\n"
				+ "\n"
				+ "    # redefinition.stepMap: 2-1-2\n"
				+ "    @ID-1\n"
				+ "    Escenario: Se consulta un usuario existente\n"
				+ "    -----------------------------\n"
				+ "        Dado un usuario identificado por '3'\n"
				+ "        Y que se ha insertado los siguientes datos en la tabla de BBDD USER:\n"
				+ "        | ID | FIRST_NAME | LAST_NAME             |\n"
				+ "        | 3  | Pepe       | Perez                 |\n"
				+ "        | 4  | Pepe       | Perez Martinez Bordiu |\n"
				+ "        Cuando se consulta el usuario\n"
				+ "        Entonces el código de respuesta HTTP es 200\n"
				+ "        Y la respuesta es parcialmente:\n"
				+ "        ```json\n"
				+ "        {\n"
				+ "             \"firstName\": \"Pepe\" \n"
				+ "        }\n"
				+ "        ```\n"
				+ "\n"
				+ "    @ID-2\n"
				+ "    Escenario: Se consulta un usuario existente\n"
				+ "        Dado un usuario identificado por '3'\n"
				+ "        Y que se ha insertado los siguientes datos en la tabla de BBDD USER:\n"
				+ "        | ID | FIRST_NAME | LAST_NAME             |\n"
				+ "        | 4  | Pepe       | Perez Martinez Bordiu |\n"
				+ "        | 3  | Pepe       | Perez                 |\n"
				+ "        Cuando se consulta el usuario\n"
				+ "");

	}

}
