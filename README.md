# MONITORING SERVER: Servidor del proyecto de reaktor para ejecutar acciones sobre ordenadores

# Descripción del proyecto

Monitoring server o servidor de monitorización es una de las partes del proyecto de reaktor que se encarga de ejecutar tareas
sobre ordenadores tales como apagado remoto, reinicio remoto, etc, mediante un identificador como puede ser su número de serie, 
número de andalucía o una pegatina identificativa, también se pueden ejecutar estas peticiones en masa utilizando las clases
dónde se encuentran, el profesor que los gestiona o los carritos dónde se almacenan

Está desarrollado en java utilizando la tecnología de springboot como medio de servidor para ejecutar peticiones, este proyecto es una 
continuación del proyecto base de reaktor ya que aparte de proporcionar información del ordenador que se ha conectado se pueden ejecutar las
acciones mencionadas desde la parte de web

## Tabla de contenidos

El proyecto se divide en 3 ramas además de los pasos previos para ejecutar el proyecto

- [Pasos previos](#pasos-previos)
- [Administración](#administración)
- [Monitorización](#monitorización)
- [Web](#web)

## Pasos previos

Antes de ejecutar el proyecto debemos de tener instalado [MySQL](https://dev.mysql.com/downloads/installer/) (MSI Installer tamaño 285.3M)
y configurar el usuario y contraseña al usuario y contraseña del fichero application.yaml

Cuando tengamos mysql instalado accedemos a <b>MySQL 8.0 Command Line Client </b>

Cuando entremos en la linea de comandos ejecutamos esta sentencia 

```
create database reaktor;
```

Nos salimos de la línea de comandos y ya tenemos las preparaciones realizadas

Loamentablemente el servidor no se puede ejecutar desde cmd porque es necesario la creación de un .jar para ello usaremos el IDE de eclipse, lo único que tenemos que hacer es importar el proyecto entero de reaktor dentro del IDE ubicar la clase que está en:

```
.\Reaktor\MonitoringServer\src\main\java\es\iesjandula\reaktor\monitoring_server
```

Y ejecutarla como java application, al principio nos arrojará una excepcion pero es porque las tablas aún no se han creado.

## Administración

La parte de administración se encarga de enviar acciones al servidor para posteriormente en [Monitorización](#Monitorización) ejecutarlas de manera automática estas peticiones son:

<ul>
    <li>Enviar comandos windows o linux a un ordenador mediante el número de serie de un ordenador, clase o carrito mandamos comandos de cmd de windows o linux a ejecutar</li>
    <br>
    <li>Apagar un ordenador remotamente mediante el número de serie de un ordenador, clase o carrito apagamos un ordenador  </li>
    <br>
    <li>Reiniciar un ordenador remotamente mediante el número de serie de un ordenador, clase o carrito reiniciamos un ordenador  </li>
    <br>
    <li>Bloquear o desbloquear puertos USB de un ordenador mediante una clase o carrito </li>
    <br>
    <li>Mandar una petición de captura mediante una clase o carrito los ordenadores realizan una captura de pantalla de lo que se esta mostrando en su pantalla en el momento</li>
    <br>
    <li>Obtener una carpeta comprimida en <b>.zip</b> de las capturas sacadas con la petición anterior</li>
    <br>
    <li>Mandar un archivo de configuración o ejecutable .cfg .exe .exec o .msi a ejecutar, estos archivos pueden tener software o configuración sobre el pc, se manda usando una clase, un carrito o la planta donde se encuentra la clase</li>
    <br>
    <li>Mandar una aplicación software a instalar o desinstalar mediante una clase o carrito </li>
    <br> 
    <li>Editar un ordenador existente usando el número de serie o número de andalucía o la pegatina identificativa del ordenador afectado</li>
</ul>

IMPORTANTE: Hay que ser <b>administrador</b> para realizar todas estas peticiones

## Monitorización

La parte de monitorización se encarga de recibir las peticiones hechas en [Administración](#administración) y ejecutarlas automáticamente realizando la acción pedida, estas peticiones son:

<ul>
    <li>Obtener todas las acciones realizadas en administración de un ordenador en concreto usando el número de serie como identificador</li>
    <br>
    <li>Obtener los ficheros ejecutables o de configuración de un ordenador en concreto usando el número de serie como identificador</li>
    <br>
    <li>Obtener y ejecutar la petición de captura de pantalla de un ordenador usando su número de serie como identificador</li>
    <br>
    <li>Activar la captura de pantalla periódica, pasando un número de serie de un ordenador, ese ordenador hace captura y la manda cada cierto tiempo para tener un seguimiento real</li>
    <br>
    <li>Mandar y actualizar periodicamente la información de un ordenador usando su número de serie o su número de andalucía o su número identificador </li>
    <br>
    <li>Mostrar el estado de las peticiones del ordenador pasandole su número de serie para identificarlo </li>
    <br>

</ul>

## Web

La parte de web se encarga de mostrar los ordenadores
conectados y sus componentes además de la posibilidad de
obtener un fichero .zip que contiene las capturas hechas por
el servidor usando peticiones

## Créditos

Este proyecto es una continuación del proyecto base Reaktor realizado por:

- [Alejandro Aljarilla Castro](https://github.com/Aljarilla11)
- [Francisco Benítez Chico](https://www.linkedin.com/in/franciscobenitezchico/)
- [Neil Hernández Salvador](https://www.linkedin.com/in/neilhdez/)

Los alumnos de 2DAM se encargaron de desarrollar esta parte del proyecto supervisado por el profesor D.Francisco Benítez Chico.