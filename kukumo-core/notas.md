Dado que el objetivo es que Kukumo corra como servicio 
y pueda servir varias peticiones al mismo tiempo, hay que 
intentar que sea lo mas seguro a nivel de hilos. La mejor opcion
es que se cree una instancia de Kukumo para cada ejecucion, 
y que sean independientes unas de otras.
En el caso de que esto penalizara mucho los tiempos de inicializacion,
podremos plantear hacer algunas partes estaticas, como el ExtensionManager.


De entrada, hay una configuracion core built-in.
Con dicha configuracion, se construye un objeto Contributions
que tendra acceso a todas las contribuciones hechas por los plugins.

El siguiente paso es procesar la configuracion de ejecucion recibida,
tanto por parametros de entrada como configurado por fichero. 
En concreto, esta configuracion extra detallara que plugins estaran
activos para la ejecucion en curso. Con esto, se creara un nuevo 
objeto Contributions refinado, que ofrecera un subconjunto de 
contribuciones.

El proceso de ejecucion consta de tres fases:

1. Preparacion
    1. Descubrir las fuentes de test
    2. Construir un arbol de nodos para cada fuente
    3. Fusionar los arboles
    4. Aplicar transformaciones de plugins
    5. Aplicar transformaciones finales (filtrado y limpieza)
2. Ejecucion
   1. Decidir en que hilo se ejecutara cada caso de test
   2. Construir un backend (mini entorno de ejecucion) para cada caso
   de test. Aqui entrara en juego la posible configuracion local del caso de test
      (y de la heredada de nodos superiores).
   Se crearan nuevas instancias de los StepContribution para cada backend.
   3. Construir un paso de backend (un runnable dentro del mini entorno de
   ejecuion) para cada paso de test. Si no se pudiera (lo escrito no
   corresponde a ningun paso, por ejemplo), el paso de backend se creara
   igualmente pero su runnable consistira directamente en lanzar una 
   excepcion WrongStepDefinition.
   4. Si alguno de los pasos de backend es 'paso indefinido', 
   el backend no ejecutara ninguno de los pasos previos ni posteriores (no tendria 
   sentido ya que el test fallara igualmente).
   5. Segun la configuracion, el backend decidira si, en caso de fallar un paso, 
   los posteriores se deberan seguir ejecutando o no.
3. Post-procesamiento
   1. Obtener un objeto que represente la ejecucion. Dicho objeto tendra
   meta-datos de ejecucion (usuario, fecha-hora, etc.) y el arbol de nodos 
   cada uno con su estado.
   2. Comprobar si hay plugins que hagan post-procesamiento, e invocarlos
      ( a ser posible en paralelo).
   