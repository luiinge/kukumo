
Este plugin proporciona un conjunto de pasos para interactuar con una API RESTful.

---

**Configuración**:
- [`rest.baseURL`](#restbaseurl)
- [`rest.contentType`](#restcontenttype)
- [`rest.httpCodeThreshold`](#resthttpcodethreshold)
- [`rest.timeout`](#resttimeout)
- [`rest.oauth2.url`](#restoauth2url)
- [`rest.oauth2.clientId`](#restoauth2clientid)
- [`rest.oauth2.clientSecret`](#restoauth2clientsecret)
- [`rest.oauth2.cached`](#restoauth2cached)
- [`rest.oauth2.parameters`](#restoauth2parameters)
- [`rest.config.multipart.subtype`](#restconfigmultipartsubtype)
- [`rest.config.redirect.follow`](#restconfigredirectfollow)
- [`rest.config.redirect.allowCircular`](#restconfigredirectallowcircular)
- [`rest.config.redirect.rejectRelative`](#restconfigredirectrejectrelative)
- [`rest.config.redirect.max`](#restconfigredirectmax)


**Pasos**:
- [Definir tipo de contenido](#definir-tipo-de-contenido)
- [Definir URL base](#definir-url-base)
- [Definir servicio](#definir-servicio)
- [Definir identificador](#definir-identificador)
- [Definir parámetros o cabeceras](#definir-par%C3%A1metros-o-cabeceras)
- [Definir parámetros o cabeceras (tabla)](#definir-par%C3%A1metros-o-cabeceras-tabla)
- [Definir timeout](#definir-timeout)
- [Definir umbral de códigos HTTP](#definir-umbral-de-c%C3%B3digos-http)
- [Definir autenticación básica](#definir-autenticaci%C3%B3n-b%C3%A1sica)
- [Definir autenticación oauth2](#definir-autenticaci%C3%B3n-oauth2)
- [Definir autenticación oauth2 por token](#definir-autenticaci%C3%B3n-oauth2-por-token)
- [Definir autenticación oauth2 por token (fichero)](#definir-autenticaci%C3%B3n-oauth2-por-token-fichero)
- [Definir autenticación oauth2 por credenciales](#definir-autenticaci%C3%B3n-oauth2-por-credenciales)
- [Definir autenticación oauth2 por cliente](#definir-autenticaci%C3%B3n-oauth2-por-cliente)
- [Borrar autenticación](#borrar-autenticaci%C3%B3n)
- [Definir subtipo multiparte](#definir-subtipo-multiparte)
- [Definir archivo adjunto](#definir-archivo-adjunto)
- [Definir archivo adjunto (fichero)](#definir-archivo-adjunto-fichero)
- [Realizar llamada GET](#realizar-llamada-get)
- [Realizar llamada DELETE](#realizar-llamada-delete)
- [Realizar llamada PUT con mensaje](#realizar-llamada-put-con-mensaje)
- [Realizar llamada PUT con mensaje (fichero)](#realizar-llamada-put-con-mensaje-fichero)
- [Realizar llamada PATCH](#realizar-llamada-patch)
- [Realizar llamada PATCH con mensaje](#realizar-llamada-patch-con-mensaje)
- [Realizar llamada PATCH con mensaje (fichero)](#realizar-llamada-patch-con-mensaje-fichero)
- [Realizar llamada POST](#realizar-llamada-post)
- [Realizar llamada POST con mensaje](#realizar-llamada-post-con-mensaje)
- [Realizar llamada POST con mensaje (fichero)](#realizar-llamada-post-con-mensaje-fichero)
- [Comprobar código HTTP de respuesta](#comprobar-c%C3%B3digo-http-de-respuesta)
- [Comprobar mensaje de respuesta](#comprobar-mensaje-de-respuesta)
- [Comprobar mensaje de respuesta (fichero)](#comprobar-mensaje-de-respuesta-fichero)
- [Comprobar fragmento de la respuesta](#comprobar-fragmento-de-la-respuesta)
- [Comprobar tipo de contenido de la respuesta](#comprobar-tipo-de-contenido-de-la-respuesta)
- [Comprobar tamaño de la respuesta](#comprobar-tama%C3%B1o-de-la-respuesta)
- [Comprobar cabecera](#comprobar-cabecera)
- [Comprobar esquema de la respuesta](#comprobar-esquema-de-la-respuesta)
- [Comprobar esquema de la respuesta (fichero)](#comprobar-esquema-de-la-respuesta-fichero)

---


## Configuración

---
###  `rest.baseURL`
Establece la URL base para las llamadas REST. Esto es equivalente al paso `{word} como el tipo de contenido REST` si se 
prefiere una declaración más descriptiva.

Ejemplo:
```yaml
rest:
  baseURL: https://example.org/api/v2
```

---
### `rest.contentType`
Establece el tipo de contenido que se enviará en la cabecera de las llamadas REST.
Los valores aceptados son:

| literal    | valor de la cabecera `content-type`                                    |
|------------|------------------------------------------------------------------------|
| `ANY`      | `*/*`                                                                  |
| `TEXT`     | `text/plain`                                                           |
| `JSON`     | `application/json, application/javascript, text/javascript, text/json` |
| `XML`      | `application/xml, text/xml, application/xhtml+xml`                     |
| `HTML`     | `text/html`                                                            |
| `URLENC`   | `application/x-www-form-urlencoded`                                    |
| `BINARY`   | `application/octet-stream`                                             |


El valor por defecto es `JSON`.

Ejemplo:
```yaml
rest:
  contentType: XML
```

---
### `rest.httpCodeThreshold`

Establece un límite a los códigos de respuesta HTTP. Cada vez que una llamada REST retorne un código HTTP igual o 
superior a este valor, el paso se marcará como fallido automáticamente, sin comprobar ninguna otra condición.

El valor por defecto es `500`.

Ejemplo:
```yaml
rest:
  httpCodeThreshold: 999
```

---
### `rest.timeout`

Establece un tiempo máximo de respuesta (en milisegundos) para las siguientes peticiones HTTP. En el caso de exceder el 
tiempo indicado se detendrá la llamada y se producirá un error.

El valor por defecto es `60000`.

Ejemplo:
```yaml
rest:
  timeout: 10000
```

---
### `rest.oauth2.url`

Establece el servicio de autenticación [OAuth 2.0][oauth2] que se usará para generar el token que se enviará en la 
cabecera HTTP `Authorization` de las llamadas REST.

Ejemplo:
```yaml
rest:
  oauth2: 
    url: https://accounts.google.com/o/oauth2/auth
```

---
### `rest.oauth2.clientId`
Establece el parámetro `clientId` para el servicio de autenticación [OAuth 2.0][oauth2] definido por el valor de la 
propiedad de configuración `rest.oauth2.url`.

Ejemplo:
```yaml
rest:
  oauth2: 
    clientId: WEB_APP
```

---
### `rest.oauth2.clientSecret`
Establece el parámetro `clientSecret` para el servicio de autenticación [OAuth 2.0][oauth2] definido por el valor de la 
propiedad de configuración `rest.oauth2.url`.

Ejemplo:
```yaml
rest:
  oauth2: 
    clientSecret: ABRACADABRAus1ZMGHvq9R
```

---
### `rest.oauth2.cached`
Establece si el token recuperado se guarda en caché para evitar llamadas recurrentes al servicio oauth si los datos son 
los mismos.

El valor por defecto es `false`.

Ejemplo:
```yaml
rest:
  oauth2:
    cached: true
```

---
### `rest.oauth2.parameters`
Establece los parámetros por defecto de la autenticación oauth.

Ejemplo:
```yaml
rest:
  oauth2:
    parameters:
      grant_type: password
      username: pepe
      password: 1234asdf
      scope: something
```

---
### `rest.config.multipart.subtype`
Establece el subtipo de las llamadas multiparte. Los valores disponibles son:

| literal       |
|---------------|
| `form-data`   |
| `alternative` | 
| `byteranges`  | 
| `digest`      |
| `mixed`       | 
| `parallel`    | 
| `related`     |
| `report`      |
| `signed`      |
| `encrypted`   |

El valor por defecto es `form-data`.

Ejemplo:
```yaml
rest:
  config:
    multipart:
      subtype: mixed
```

---
### `rest.config.redirect.follow`
Establece si se permite seguir las redirecciones en las llamadas HTTP. 

El valor por defecto es `true`.

Ejemplo:
```yaml
rest:
  config:
    redirect:
      follow: false
```

---
### `rest.config.redirect.allowCircular`
Establece si se permite las redirecciones circulares en las llamadas HTTP.

El valor por defecto es `false`.

Ejemplo:
```yaml
rest:
  config:
    redirect:
      allowCircular: true
```

---
### `rest.config.redirect.rejectRelative`
Establece si se rechazan las redirecciones relativas en las llamadas HTTP.

El valor por defecto es `false`.

Ejemplo:
```yaml
rest:
  config:
    redirect:
      rejectRelative: true
```

---
### `rest.config.redirect.max`
Establece el número de redirecciones máximo en las llamadas HTTP.

El valor por defecto es `100`.

Ejemplo:
```yaml
rest:
  config:
    redirect:
      max: 150
```



## Pasos

---
### Definir tipo de contenido
```
{type} como el tipo de contenido REST
```
Establece el tipo de contenido de la API en la cabecera `content-type`. Este paso es equivalente a configurar la 
propiedad [`rest.contentType`](#restcontenttype). 

#### Parámetros:
| Nombre | Wakamiti type | Descripción        |
|--------|---------------|--------------------|
| `type` | `word`        | La URL de conexión |

#### Ejemplos:
```gherkin
  Dado XML como el tipo de contenido REST
```

---
### Definir URL base
```
la URL base {url}
```
Establece la ruta base de la API. Este paso es equivalente a configurar la propiedad [`rest.baseURL`](#restbaseurl). 

#### Parámetros:
| Nombre | Wakamiti type | Descripción |
|--------|---------------|-------------|
| `url`  | `url`         | URL base    |

#### Ejemplos:
```gherkin
  Dada la URL base https//example.org/api
```

---
### Definir servicio
```
el servicio REST {service}
```
Establece la ruta del servicio a probar. Se concatenará al valor de la [url base](#definir-url-base).

#### Parámetros:
| Nombre    | Wakamiti type | Descripción  |
|-----------|---------------|--------------|
| `service` | `text`        | Segmento URL |

#### Ejemplos:
```gherkin
  Dado el servicio REST '/users`
```

---
### Definir identificador
```
* identificad(o|a|os|as) por {text}
```
Establece un identificador de recurso REST para ser usado por el servicio. Se concatenará al valor de la 
[url base](#definir-url-base) y del [servicio](#definir-servicio) en concreto.

#### Parámetros:
| Nombre | Wakamiti type | Descripción                 |
|--------|---------------|-----------------------------|
| `text` | `text`        | Un identificador de recurso |

#### Ejemplos:
```gherkin
  Dado un usuario identificado por 'john'
```
```gherkin
  Dado un libro identificado por '978-3-16-148410-0'
```

---
### Definir parámetros o cabeceras 
```
el parámetro de (solicitud|búsqueda|ruta) {name} con el valor {value}
```
```
las cabecera {name} con el valor {value}
```
Establece una cabecera o parámetro de petición, búsqueda o ruta REST. Los parámetros de petición se enviarán como 
datos de formulario wn las llamadas POST, los parámetros de búsqueda se concatenarán a la URL de la petición tras la 
ruta (p.e. `/user?param1=abc&param2=123`), y los parámetros de ruta reemplazarán los fragmentos de la ruta del servicio indicados 
con llaves `{}`.

##### Parámetros:
| Nombre  | Wakamiti type | Descripción                     |
|---------|---------------|---------------------------------|
| `name`  | `text`        | Nombre del parámetro o cabecera |
| `value` | `text`        | Valor del parámetro o cabecera  |

##### Ejemplos:
```gherkin
  Dado el parámetro de solicitud 'age' con el valor '13'
  Cuando se envía al servicio la información
```
```gherkin
  Dado el parámetro de búsqueda 'city' con el valor 'Valencia'
  Cuando se realiza la búsqueda del usuario
```
```gherkin
  Dado el servicio 'user/{usuario}/items'
  Y el parámetro de ruta 'usuario' con el valor '25'
```
```gherkin
  Dada la cabeceras 'Keep-alive' con el valor '1200'
```

---
### Definir parámetros o cabeceras (tabla)
```
los siguientes parámetros de (solicitud|búsqueda|ruta):
```
```
las siguientes cabeceras:
```
Establece varias cabeceras o parámetros de petición, búsqueda o ruta REST. Los parámetros de petición se enviarán como
datos de formulario, los parámetros de búsqueda se concatenarán a la URL de la petición tras la ruta (p.e.
`/user?param1=abc&param2=123`), y los parámetros de ruta reemplazarán los fragmentos de la ruta del servicio indicados
con llaves `{}`.

##### Parámetros:
| Nombre | Wakamiti type | Descripción                                   |
|--------|---------------|-----------------------------------------------|
|        | `table`       | Una tabla con las columnas `nombre` y `valor` |

##### Ejemplos:
```gherkin
  Dados los siguiente parámetros de solicitud:
    | nombre | valor    |
    | age    | 13       |
    | city   | Valencia |
  Cuando se envía al servicio la información
```
```gherkin
  Dados los siguiente parámetros de búsqueda:
    | nombre | valor    |
    | age    | 13       |
    | city   | Valencia |
  Cuando se realiza la búsqueda del usuario
```
```gherkin
  Dado el servicio 'user/{usuario}/items/{item}'
  Y los siguientes parámetros de ruta:
    | nombre  | valor    |
    | usuario | 25       |
    | item    | 7        |
```
```gherkin
  Dadas las siguientes cabeceras:
    | nombre       | valor |
    | Age          | 3600  |
    | Keep-Alive   | 1200  |
```

---
### Definir timeout
```
un timeout de {int} (mili)segundos
```
Establece un tiempo máximo de respuesta (en segundos o milisegundos) para las siguientes peticiones HTTP. En el caso de 
exceder el tiempo indicado se detendrá la llamada y se producirá un error.

##### Parámetros:
| Nombre | Wakamiti type | Descripción       |
|--------|---------------|-------------------|
| `int`  | `int`         | El tiempo máximo  |

##### Ejemplos:
```gherkin
  Dado un timeout de 12000 milisegundos
```
```gherkin
  Dado un timeout de 10 segundos
```

---
### Definir umbral de códigos HTTP
```
(que) toda petición se considera fallida si su código HTTP {matcher}
```
Establece una validación general para el código HTTP de todas las respuestas siguientes. Es similar a la propiedad de 
configuración [`rest.httpCodeTreshold`](#resthttpcodethreshold) pero con una validación de enteros personalizada.

##### Parámetros:
| Nombre    | Wakamiti type        | Descripción              |
|-----------|----------------------|--------------------------|
| `matcher` | `integer-assertion`  | [Comparador][1] numérico |

##### Ejemplo:
```gherkin
  * toda petición se considera fallida si su código HTTP es igual o mayor que 500
```

---
### Definir autenticación básica
```
(que) el servicio usa autenticación básica con las credenciales {username}:{password}
```
Establece las credenciales de autenticación básica que se enviarán en la cabecera HTTP `Authorization`.

##### Parámetros:
| Nombre     | Wakamiti type | Descripción       |
|------------|---------------|-------------------|
| `username` | `text`        | Nombre de usuario |
| `password` | `text`        | Contraseña        |

##### Ejemplos:
```gherkin
  Dado que el servicio usa autenticación básica con las credenciales 'us1532':'xxxxx'
```

---
### Definir autenticación oauth2
```
(que) el servicio usa autenticación oauth
```
Establece el token de autenticación "bearer" que se enviará en la cabecera `Authorization`, que se recupera previamente
del servicio oauth2 configurado ([url](#restoauth2url), [clientId](#restoauth2clientid),
[clientSecret](#restoauth2clientsecret), [parámetros](#restoauth2parameters)), para las siguientes peticiones.

##### Ejemplos:
```gherkin
  Dado que el servicio usa autenticación oauth
```

---
### Definir autenticación oauth2 por token
```
(que) el servicio usa autenticación oauth con el token {token}
```
Establece el token de autenticación "bearer" que se enviará en la cabecera `Authorization` para las siguientes 
peticiones.

##### Parámetros:
| Nombre  | Wakamiti type | Descripción            |
|---------|---------------|------------------------|
| `token` | `text`        | token de autenticación |

##### Ejemplos:
```gherkin
  Dado que el servicio usa autenticación oauth con el token 'hudytw9834y9cqy32t94'
```

---
### Definir autenticación oauth2 por token (fichero)
```
(que) el servicio usa autenticación oauth con el token del fichero {file}
```
Establece el token de autenticación "bearer" que se enviará en la cabecera `Authorization` para las siguientes llamadas, 
obtenido desde un fichero.

##### Parámetros:
| Nombre | Wakamiti type | Descripción                           |
|--------|---------------|---------------------------------------|
| `file` | `file`        | Fichero con el token de autenticación |

##### Ejemplo:
```gherkin
  Dado que el servicio usa autenticación oauth con el token del fichero 'token.txt'
```

---
### Definir autenticación oauth2 por credenciales
```
(que) el servicio usa autenticación oauth con las credenciales {username}:{password}
```
```
(que) el servicio usa autenticación oauth con las credenciales {username}:{password} y los siguientes parámetros:
```
Establece el token de autenticación "bearer" que se enviará en la cabecera `Authorization`, que se recupera previamente 
del servicio oauth2 configurado ([url](#restoauth2url), [clientId](#restoauth2clientid), 
[clientSecret](#restoauth2clientsecret)), usando las credenciales indicadas, para las siguientes peticiones. 

También se pueden añadir más parámetros adicionales admitidos por `Oauth` mediante una tabla.

##### Parámetros:
| Nombre     | Wakamiti type | Descripción                                   |
|------------|---------------|-----------------------------------------------|
| `username` | `text`        | Nombre de usuario                             |
| `password` | `text`        | Contraseña                                    |
|            | `table`       | Una tabla con las columnas `nombre` y `valor` |

##### Ejemplos:
```gherkin
  Dado que el servicio usa autenticación oauth con las credenciales 'us1532':'xxxxx'
```

```gherkin
  Dado que el servicio usa autenticación oauth con las credenciales 'us1532':'xxxxx' y los siguientes parámetros:
    | name  | value     |
    | scope | something |
```

---
### Definir autenticación oauth2 por cliente
```
(que) el servicio usa autenticación oauth
```
```
(que) el servicio usa autenticación oauth con los siguientes parámetros:
```
Establece el token de autenticación "bearer" que se enviará en la cabecera `Authorization`, que se recupera previamente
del servicio oauth2 configurado ([url](#restoauth2url), [clientId](#restoauth2clientid),
[clientSecret](#restoauth2clientsecret)), usando los datos del cliente, para las siguientes peticiones.

También se pueden añadir más parámetros adicionales admitidos por `Oauth` mediante una tabla.

##### Parámetros:
| Nombre     | Wakamiti type | Descripción                                   |
|------------|---------------|-----------------------------------------------|
|            | `table`       | Una tabla con las columnas `nombre` y `valor` |


##### Ejemplos:
```gherkin
  Dado que el servicio usa autenticación oauth
```

```gherkin
  Dado que el servicio usa autenticación oauth con los siguientes parámetros:
    | name  | value     |
    | scope | something |
```

---
### Borrar autenticación
```
(que) el servicio no usa autenticación
```
Elimina la cabecera con la autenticación.

##### Ejemplos:
```gherkin
  Dado que el servicio no usa autenticación
```

### Definir subtipo multiparte
```
{type} como subtipo multiparte
```
Establece el subtipo por defecto de las llamadas multiparte. Este paso es equivalente a configurar la propiedad 
[`rest.config.multipart.subtype`](#restconfigmultipartsubtype).

##### Parámetros:
| Nombre     | Wakamiti type | Descripción        |
|------------|---------------|--------------------|
| `type`     | `text`        | Subtipo multiparte |

##### Ejemplos:
```gherkin
  Dado 'mixed' como subtipo multiparte
```

---
### Definir archivo adjunto
```
(que) se incluye el fichero adjunto {name} con los siguientes datos:
```
Indica el texto que se incluirá como fichero adjunto en datos de formulario.

##### Parámetros:
| Nombre | Wakamiti type | Descripción          |
|--------|---------------|----------------------|
| {name} | `text`        | Nombre de control    |
|        | `document`    | Contenido a adjuntar |

##### Ejemplos:
```gherkin
  Dado que se incluye el fichero adjunto 'fichero' con los siguientes datos:
    """
    Contenido del fichero
    """
```

---
### Definir archivo adjunto (fichero)
```
(que) se incluye el fichero adjunto {name} con el contenido del fichero {file}
```
Indica el fichero cuyo contenido se incluirá como fichero adjunto en datos de formulario.

##### Parámetros:
| Nombre | Wakamiti type | Descripción                         |
|--------|---------------|-------------------------------------|
| `file` | `file`        | Fichero con el contenido a adjuntar |

##### Ejemplos:
```gherkin
  Dado que se incluye el fichero adjunto 'fichero' con el contenido del fichero 'data.txt'
```


---
### Realizar llamada GET
```
se realiza la búsqueda *
```
```
se consulta(n) *
```
Envía una petición `GET` al servicio con los parámetros definidos previamente.

##### Ejemplos:
```gherkin
  Dado el servicio REST '/users'
  Y los siguientes parámetros de búsqueda:
    | name | value    |
    | age  | 13       |
    | city | Valencia |
  Cuando se realiza la búsqueda de usuarios
```
```gherkin
  Dado el servicio REST '/users'
  Y un usuario identificado por '123'
  Cuando se consulta el usuario
```

---
### Realizar llamada DELETE
```
se elimina(n) *
```
Envía una petición `DELETE` al servicio y recurso REST definido previamente.

##### Ejemplos:
```gherkin
  Dado el servicio REST '/users'
  Y un usuario identificado por '123'
  Cuando se elimina el usuario
```

---
### Realizar llamada PUT con mensaje
```
se reemplaza(n) * con los siguientes datos:
```
Envía una petición `PUT` al servicio y recurso REST definido previamente. El cuerpo de la petición será el contenido 
indicado a continuación.

##### Parámetros:
| Nombre | Wakamiti type | Descripción              |
|--------|---------------|--------------------------|
|        | `document`    | El cuerpo de la petición |

##### Ejemplos:
```gherkin
  Dado el servicio REST '/users'
  Y un usuario identificado por '123'
  Cuando se reemplaza el usuario con los siguientes datos:
    """json
    {
        "firstName": "John",
        "lastName": "Doe",
        "birthDate": "1980-02-20",
        "address": "221B, Baker Street"
    }
    """
```

---
### Realizar llamada PUT con mensaje (fichero)
```
se reemplaza(n) * con los datos del fichero {file}
```
Envía una petición `PUT` al servicio y recurso REST definido previamente. El cuerpo de la petición será el contenido del 
fichero indicado.

##### Parámetros:
| Nombre | Wakamiti type | Descripción                          |
|--------|---------------|--------------------------------------|
| `file` | `file`        | Fichero con el cuerpo de la petición |

##### Ejemplos:
```gherkin
  Dado el servicio REST '/users'
  Y un usuario identificado por '123'
  Cuando se reemplaza el usuario con los datos del fichero 'data/user123.json'
```

---
### Realizar llamada PATCH
```
se modifica(n) * 
```
Envía una petición `PATCH` al servicio y recurso REST definido previamente. 

##### Ejemplos:
```gherkin
  Dado el servicio REST '/users'
  Y un usuario identificado por '123'
  Y los siguientes parámetros de búsqueda:
    | name | value    |
    | age  | 13       |
    | city | Valencia |
  Cuando se modifica el usuario
```

---
### Realizar llamada PATCH con mensaje
```
se modifica(n) * con los siguientes datos:
```
Envía una petición `PATCH` al servicio y recurso REST definido previamente. El cuerpo de la petición será el contenido 
indicado a continuación.

##### Parámetros:
| Nombre | Wakamiti type | Descripción              |
|--------|---------------|--------------------------|
|        | `document`    | El cuerpo de la petición |

##### Ejemplos:
```gherkin
  Dado el servicio REST '/users'
  Y un usuario identificado por '123'
  Cuando se modifica el usuario con los siguientes datos:
    """json
    {
        "firstName": "Jim"
    }
    """
```

---
### Realizar llamada PATCH con mensaje (fichero)
```
se modifica(n) * con los datos del fichero {file}
```
Envía una petición `PATCH` al servicio y recurso REST definido previamente. El cuerpo de la petición será el contenido 
del fichero indicado.

##### Parámetros:
| Nombre | Wakamiti type | Descripción                          |
|--------|---------------|--------------------------------------|
| `file` | `file`        | Fichero con el cuerpo de la petición |

##### Ejemplos:
```gherkin
  Dado el servicio REST '/users'
  Y un usuario identificado por '123'
  Cuando se modifica el usuario con los datos del fichero 'data/user123.json'
```

---
### Realizar llamada POST
```
se crea(n) *
```
```
se envía al servicio la información
```
Envía una petición `POST` al servicio definido previamente.

##### Ejemplo:
```gherkin
  Dado el servicio REST '/users'
  Dados los siguiente parámetros de solicitud:
    | nombre | valor    |
    | age    | 13       |
    | city   | Valencia |
  Cuando envía al servicio la información
```

---
### Realizar llamada POST con mensaje
```
se crea(n) * con los siguientes datos:
```
```
se envía al servicio los siguientes datos:
```
Envía una petición `POST` al servicio definido previamente. El cuerpo de la petición se rellenará con el contenido 
indicado a continuación.

##### Parámetros:
| Nombre | Wakamiti type | Descripción              |
|--------|---------------|--------------------------|
|        | `document`    | El cuerpo de la petición |

##### Ejemplos:
```gherkin
  Cuando se crea un usuario con los siguientes datos:
    """json
    {
        "firstName": "John",
        "lastName": "Doe",
        "birthDate": "1980-02-20",
        "address": "221B, Baker Street"
    }
    """
```
```gherkin
  Cuando se envía al servicio los siguientes datos:
    """json
    {
        "date": "2021-10-30"
    }
    """
```

---
### Realizar llamada POST con mensaje (fichero)
```
se crea(n) * con los datos del fichero {file}
```
```
se envía al servicio los datos del fichero {file}
```
Envía una petición `POST` al servicio definido previamente. El cuerpo de la petición se rellenará con el contenido del 
fichero indicado.

##### Parámetros:
| Nombre | Wakamiti type | Descripción                          |
|--------|---------------|--------------------------------------|
| `file` | `file`        | Fichero con el cuerpo de la petición |

##### Ejemplos:
```gherkin
  Cuando se crea un usuario con los datos del fichero 'data/user123.json'
```
```gherkin
  Cuando se envía al servicio los datos del fichero 'booking.json'
```

---
### Comprobar código HTTP de respuesta
```
el código de respuesta HTTP {matcher}
```
Comprueba que el código HTTP de la última respuesta satisface una validación de enteros.

##### Parámetros:
| Nombre    | Wakamiti type        | Descripción               |
|-----------|----------------------|---------------------------|
| `matcher` | `integer-assertion`  | Una validación de enteros |

##### Ejemplos:
```gherkin
  Entonces el código de respuesta HTTP es 201
```


---
### Comprobar mensaje de respuesta
```
la respuesta es exactamente:
```
Valida que el cuerpo de la respuesta sea exacto al indicado, incluyendo el orden de los campos.
```
la respuesta es exactamente \(en cualquier orden\):
```
Valida que el cuerpo de la respuesta sea exacto al indicado, pero pueden llegar los campos en diferente orden.
```
la respuesta es parcialmente:
```
Valida que el cuerpo de la respuesta incluya, al menos, los campos indicados.

##### Parámetros:
| Nombre | Wakamiti type | Descripción           |
|--------|---------------|-----------------------|
|        | `document`    | El contenido esperado |

##### Ejemplos:
```gherkin
  Entonces la respuesta es exactamente:
    """json
    {
        "age": 23,
        "name": "John"
    }
    """
```

---
### Comprobar mensaje de respuesta (fichero)
```
la respuesta es exactamente el contenido del fichero {file}
```
Valida que el cuerpo de la respuesta sea exacto al indicado en el fichero, incluyendo el orden de los campos.
```
la respuesta es exactamente el contenido del fichero {file} \(en cualquier orden\)
```
Valida que el cuerpo de la respuesta sea exacto al indicado en el fichero, pero pueden llegar los campos en diferente orden.
```
la respuesta es parcialmente el contenido del fichero {file}
```
Valida que el cuerpo de la respuesta incluya, al menos, los campos indicados en el fichero.

##### Parámetros:
| Nombre | Wakamiti type | Descripción          |
|--------|---------------|----------------------|
| `file` | `file`        | Un fichero existente |

##### Ejemplos:
```gherkin
  Entonces la respuesta es parcialmente el contenido del fichero 'data/response1.json'
```

---
### Comprobar fragmento de la respuesta
```
el (texto|entero|decimal) del fragmento de la respuesta {fragment} {matcher}
```
Valida el valor (*texto*, *entero* o *decimal*) de un fragmento del cuerpo de respuesta, localizado mediante una ruta dada
(usando [JSONPath][jsonpath] o [XPath][xpath] dependiendo del tipo de contenido).

##### Parámetros:
| Nombre     | Wakamiti type  | Descripción                 |
|------------|----------------|-----------------------------|
| `fragment` | `text`         | Una ruta JSONPath or XPath  |
| `matcher`  | `*-assertion`  | El comparador del fragmento |
`*`: `text`, `integer` o `decimal`, dependiendo del tipo indicado en el paso.

##### Ejemplos:
```gherkin
  Entonces el decimal del fragmento de la respuesta 'users[1].account.availableMoney` es mayor que 23.57
```
```gherkin
  Entonces el texto del fragmento de la respuesta 'users[1].name` no es 'John'
```


---
### Comprobar tipo de contenido de la respuesta
```
el tipo de contenido de la respuesta es {word}
```
Valida que el tipo de contenido de la última respuesta es el esperado.
Este paso equivale a validar que el valor de la cabecera `Content-Type` de la respuesta es el tipo MIME correspondiente.

##### Parámetros:
| Nombre  | Wakamiti type | Descripción                                        |
|---------|---------------|----------------------------------------------------|
| `word`  | `word`        | `ANY`,`TEXT`,`JSON`,`XML`,`HTML`,`URLENC`,`BINARY` |

##### Ejemplos:
```gherkin
  Entonces el tipo de contenido de la respuesta es JSON
```

---
### Comprobar tamaño de la respuesta
```
el tamaño de la respuesta {matcher}
```
Comprueba que la longitud en bytes de la última respuesta satisface una validación.

##### Parámetros:
| Nombre    | Wakamiti type        | Descripción               |
|-----------|----------------------|---------------------------|
| `matcher` | `integer-assertion`  | Una validación de enteros |

##### Ejemplos:
```gherkin
  Entonces el tamaño de la respuesta es menor que 500
```

---
### Comprobar cabecera
```
el (texto|entero|decimal) de la cabecera de la respuesta {name} {matcher}
```
Comprueba que una determinada cabecera HTTP en la última respuesta satisface una validación de *texto*, *entero* o 
*decimal*.

##### Parámetros:
| Nombre    | Wakamiti type  | Descripción                         |
|-----------|----------------|-------------------------------------|
| `name`    | `text`         | Nombre de la cabecera               |
| `matcher` | `*-assertion`  | [Comparador][1] de texto o numérico |
`*`: `text`, `integer` o `decimal`, dependiendo del tipo indicado en el paso.

##### Ejemplos:
```gherkin
  Entonces el texto de la cabecera de la respuesta Content-Encoding contiene 'gzip'
```
```gherkin
  Entonces el entero de la cabecera de la respuesta Age es mayor que 10
```

---
### Comprobar esquema de la respuesta
```
la respuesta cumple el siguiente esquema:
```
Valida que la estructura del cuerpo de la respuesta REST satisface el esquema proporcionado a continuación. Los formatos 
de esquema aceptados son [JSON Schema][jsonschema] para respuestas JSON y [XML Schema][xmlschema] para las respuestas 
XML (en función de la cabecera de respuesta HTTP `Content-Type`).

##### Parámetros:
| nombre | Wakamiti type | descripción              |
|--------|---------------|--------------------------|
|        | `document`    | JSON Schema o XML Schema |

##### Ejemplo:
```gherkin
  Entonces la respuesta cumple el siguiente esquema:
    """json
     {
       "$schema": "http://json-schema.org/draft-04/schema#",
       "type": "object",
       "properties": {
         "id":         { "type": "string", "pattern": "[a-zA-Z0-9]+" },
         "name":       { "type": "string" },
         "age":        { "type": "integer", "minimum": 5 },
         "vegetables": {
           "type": "array",
           "items": [ {
               "type": "object",
               "properties": {
                 "id":          { "type": "integer" },
                 "description": { "type": "string"  }
               },
               "required": [ "id", "description" ]
           } ]
         },
         "contact": {
           "type": "object",
           "properties": {
             "email": { "type": "string", "pattern": "^[a-zA-Z0-9]+@[a-zA-Z0-9\.]+$" }
           }
         }
       }
     }
    """
```

---
### Comprobar esquema de la respuesta (fichero)
```
la respuesta cumple el esquema del fichero {file}
```
Valida que la estructura del cuerpo de la respuesta REST satisface un esquema proporcionado por fichero. Los formatos de 
esquema aceptados son [JSON Schema][jsonschema] para respuestas JSON y [XML Schema][xmlschema] para las respuestas XML 
(en función de la cabecera de respuesta HTTP `Content-Type`).

##### Parámetros:
| nombre | Wakamiti type | descripción                                 |
|--------|---------------|---------------------------------------------|
|        | `file`        | Fichero con un JSON Schema o un XML Schema  |

##### Ejemplo:
```gherkin
  Entonces la respuesta cumple el esquema del fichero 'data/user-schema.json'
```



[oauth2]: https://datatracker.ietf.org/doc/html/rfc6749 (OAuth 2.0)
[jsonschema]: https://json-schema.org/ (JSON Schema)
[jsonpath]: https://goessner.net/articles/JsonPath/
[xmlschema]: https://www.w3.org/2001/XMLSchema (XML Schema)
[xpath]: https://en.wikipedia.org/wiki/XPath (XPath)
[1]: wakamiti/architecture#comparadores
