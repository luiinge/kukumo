---
title: File Uploader
date: 2023-05-29
slug: /plugins/fileuploader
---

Este plugin registra un observador de eventos que se dispara cuando se genera un fichero de 
salida, e intenta subir dicho fichero a una ubicación remota usando el protocolo FTP (o FTPS).
Más específicamente, se dispara ante cualquier evento de tipo `STANDARD_OUTPUT_FILE_WRITTEN`,
`TEST_CASE_OUTPUT_FILE_WRITTEN` o `REPORT_OUTPUT_FILE_WRITTEN`.


Configuración
-------------

### `fileUploader.enable` : `true`|`false`
Since: ```2.0.0```

Activa - desactiva el observador de eventos. El valor por defecto es `false`


### `fileUploader.host`
Since: ```2.0.0```

El nombre o dirección IP de la máquina a la que se van a subir los ficheros. Opcionalmenbte,
puede incluir un número de puerto, en la forma `hostname:port`

### `fileUploader.credentials.username`
Since: ```2.0.0```

El nombre de usuario usado para establecer la conexión FTP/FTPS

### `fileUploader.credentials.password`
Since: ```2.0.0```

La contraseña usada para establecer la conexión FTP/FTPS

### `fileUploader.protocol` : `ftp` | `ftps`
Since: ```2.0.0```

El protocolo específico a usar (se recomiendo `ftps`)

### `fileUploader.destinationDir`
Since: ```2.0.0```

El directorio de destino al cual los ficheros deberían subirse, dentro de la ubicación remota. 
Puede incluir variables de ruta como `%DATE%`, `%TIME%`, or `%execID%`


---

Esta configuración global se aplica a todos los tipos de evento recibidos. Sin embargo, existe la 
posibilidad de ajustar una o más propiedades con valores especñificos según el tipo de evento. Por
ejemplo, la siguiente configuración usaría los mismos parámetros de conexión pero subiendo los ficheros
a diferentes directorios según el tipo de evento:

```yaml
fileUploader:
  host: 192.168.1.40
  protocol: ftps
  credentials:
    username: test
    password: testpwd
  standardOutputs:
    destinationDir: data/results
  reportOutputs:
    destinationDir: data/reports
  testCaseOutputs:
    destinationDir: data/tests/%DATE%%TIME%
```