---
title: Appium
date: 2023-06-09
slug: /plugins/appium
---

Este plugin permite usar Wakamiti para escribir escenarios que interactuen con un servidor 
[Appium](http://appium.io/docs/en/2.0/). Appium es un proyecto open-source diseñado para 
facilitar la automatización de tests UI en varias plataformas, incluyendo aplicaciones móviles.

El uso de este plugin requiere un servidor Appium en marcha, al igual que un dispositivo
virtual emulado. Más abajo hay un ejemplo de como preparar un entorno de test adecuado.


> **AVISO**
> 
> En su estado actual, este plugin es principalmente una prueba de concepto más que un plugin 
> plenamente funcional. Los pasos y la configuración indicada pueden variar en futuras versiones.



Configuracion
----------------------------------------------------------------------------------------------------

> Se puede especificar cualquier capacidad a pasar al Web Driver usando la clave adecuada con
> el formato ```appium.capabilities.xxxxxx```. En este document se describen las opciones mas relevantes,
> pero para una lista exhaustiva de las propiedades disponibles, consultar el
> [borrador W3C WebDriver](https://w3c.github.io/webdriver/#capabilities).

---

### `appium.url` 
La URL del servidor Appium

Ejemplo:
```yaml
appium:
  url: http://127.0.0.1:4723/wd/hub
```

---

### `appium.capabilities.app`
La ruta completa de la aplicación a testear

Ejemplo:
```yaml
appium:
  capabilities:
    app: ApiDemos-debug.apk
```

---

### `appium.capabilities.platformName`
El tipo de plataforma móvil que se va a testear

Ejemplo:
```yaml
appium:
  capabilities:
    platformName: Android
```

---

### `appium.capabilities.platformVersion`
La versión de la plataforma que se va a testear

Ejemplo:
```yaml
appium:
  capabilities:
    platformVersion: 11
```


---

### `appium.capabilities.appPackage`
El nombre del paquete que contiene la aplicación que se va a testear

Ejemplo:
```yaml
appium:
  capabilities:
    appPackage: io.appium.android.apis
```

---


### `appium.capabilities.appActivity`
El nombre de la actividad a testear

Ejemplo:
```yaml
appium:
  capabilities:
    appActivity: '.view.TextFields'
```


Pasos
----------------------------------------------------------------------------------------------------


---
### Seleccionar un elemento de interfaz a partir de su ID
```
el elemento de interfaz con el ID {text}
```
Selecciona un elmento que será el sujeto de los siguientes pasos.
#### Ejemplos:
```gherkin
  Dado el elemento de interfaz con el ID '3423423'
```


---
### Seleccionar un elemento de interfaz a partir de su tipo
```
el elemento de interfaz de tipo {text}
```
Selecciona un elmento que será el sujeto de los siguientes pasos.
#### Ejemplos:
```gherkin
  Dado el elemento de interfaz de tipo 'android.widget.EditText'
```


---
### Seleccionar un elemento de interfaz a partir de su ruta
```
el elemento de interfaz con ruta {text}
```
Selecciona un elmento que será el sujeto de los siguientes pasos.
#### Ejemplos:
```gherkin
  Dado el elemento de interfaz con ruta  'main.form.name'
```


---
### Teclear un texto en un elemento
```
se escribe el texto {text} en ese elemento
```
Emula la acción de introducir un texto cuando un elemento se ha seleccionado
#### Ejemplos:
```gherkin
  Cuando se escribe el texto 'John' en ese elemento
```


---
### Se pulsa sobre un elemento
```
se pulsa sobre ese elemento
```
Emula la acción de pulsar sobre un elemento
#### Ejemplos:
```gherkin
  Cuando se pulsa sobre ese elemento
```



---
### Se pulsa dos veces sobre un elemento
```
se pulsa dos veces ese elmento
```
Emula la acción de pulsar dos veces sobre un elemento
#### Ejemplos:
```gherkin
  Cuando se pulsa dos veces sobre ese elemento
```



---
### Valida el texto de un elemento
```
ese elemento contiene el valor {text}
```
Comprueba que el elemento previamente seleccionado contiene un texto específico
#### Ejemplos:
```gherkin
  Entonces ese elemento contiene el valor 'Accepted'
```


---
### Valida que un elemento está habilitado
```
ese elemento esta habilitado
```
Comprueba que el elemento previamente seleccionado está habilitado
#### Ejemplos:
```gherkin
  Entonces ese elemento esta habilitado
```



---
### Valida que un elemento está deshabilitado
```
ese elemento esta deshabilitado
```
Comprueba que el elemento previamente seleccionado está deshabilitado
#### Ejemplos:
```gherkin
  Entonces ese elemento esta deshabilitado
```


---
### Validate an element is displayed
```
ese elemento se muestra por pantalla
```
Comprueba que el elemento previamente seleccionado está siendo mostrado por pantalla
#### Ejemplos:
```gherkin
  Entonces ese elemento se muestra por pantalla
```

---
### Validate an element is not displayed
```
ese elemento no se muestra por pantalla
```
Comprueba que el elemento previamente seleccionado no está siendo mostrado por pantalla
#### Ejemplos:
```gherkin
   Entonces ese elemento no se muestra por pantalla
```


---
### Emular una llamada entrante
```
se recibe una llamada entrante con el numero {text}
```
Emula una llamada entrante de un número de teléfono específico. Solo disponible si la aplicación
a testear está siendo ejecutada en un dispositivo emulado.
#### Ejemplos:
```gherkin
  Cuando se recibe una llamada entrante con el numero '555-4324-432'
```


---
### Aceptar una llamada entrante
```
se acepta la llamada entrante
```
Acepta la llamada que está entrando en ese momento
#### Ejemplos:
```gherkin
  Cuando se acepta la llamada entrante
```


---
### Rechazar una llamada entrante
```
se rechaza la llamada entrante
```
Rechaza la llamada que está entrando en ese momento
#### Ejemplos:
```gherkin
  Cuando se rechaza la llamada entrante
```


---
### Finaliza la llamada actual
```
se finaliza la llamada
```
Finaliza (cuelga) la llamada actual
#### Ejemplos:
```gherkin
  Cuando se finaliza la llamada
```




Preparación de un entorno de pruebas
----------------------------------------------------------------------------------------------------

1. Instalar Android SDK
```
  sudo apt install android-sdk
```


2. Agregar la variable de entorno ANDROID_HOME
```
  (p.ej. $HOME/Android/Sdk )
```

3. Instalar cmdline-tools

![](android-sdk.png)

5. Instalar Appium y Appium-doctor
```
   npm install -g appium
   npm install @appium/doctor --location=global
```

4. Comprobar la instalación
```
appium-doctor --android
```
Todo deberia salir como OK, en caso contrario revisar los checks en rojo

5. Arrancar el servidor Appium
```
appium
```
El puerto por defecto es 4723

Descargar APK de prueba en:
https://github.com/appium/appium/raw/1.x/sample-code/apps/ApiDemos-debug.apk

6. Crear un dipositivo Virtual
```
$ANDROID_HOME/cmdline-tools/latest/bin/avdmanager create avd --force --name Nexus6P --abi google_apis_playstore/x86 --package 'system-images;android-30;google_apis_playstore;x86' --device "Nexus 6P"```
```

7. Iniciar el emulador Android
```
$ANDROID_HOME/emulator/emulator -avd Nexus6P
```


Si en algun momento el emulador se queda pillado y dice que ya existe una emulacion en curso,
se puede limpiar el estado con
```
$ANDROID_HOME/platform-tools/adb kill-server
```


Lo ideal seria poder lanzar todo esto de manera semiautomatica unicamente
a partir del APK, pero de momento hay que:
- Instalar Android Studio
- Crear dispositivo virtual (AVD) Por ejemplo Pixel 2 API 30 con Android 11
- Arrancar el AVD con el boton de play

