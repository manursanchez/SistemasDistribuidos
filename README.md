# Sistema de almacenamiento distribuido usando Java RMI

El objetivo de este trabajo, es el desarrollo de un sistema de almacenamiento de ficheros en la nube usando Java RMI.

Este sistema consta de tres entidades:

Entidad Servidor: controla el proceso de almacenamiento de ficheros, gestionando también los recursos de almacenaje. Para esto hace uso de tres servicios:
- Autenticación: registra y autentica las otras entidades participantes (clientes y repositorios)
- Gestión: gestiona las operaciones de los clientes en relación a los ficheros en la nube.
- Datos: este servicio hará las funciones de relación entre clientes-ficheros-repositorios-metadatos para permitir operaciones de consulta, borrado y añadido. 

Entidad Repositorio: se responsabiliza de guardar en los dispositivos de almacenamiento los ficheros que los clientes suben a la nube. Dispone de dos servicios:
- CLiente - Operador: se encarga de las operaciones de subida y/o borrado de ficheros al repositorio.
- Servidor- Operador: tiene un doble objetivo --> suministrar los métodos necesarios para que el servidor gestione el almacenamiento de cada cliente.
                                              --> se encarga de la operación de bajada desde el repositorio al cliente.

Entidad Cliente: son los usuarios que se registran en el sistema a través del Servidor para poder almacenar y gestionar los ficheros que suben.


En src/ se encuentran cuatro directorios donde se desarrollan las clases e interfaces del: servidor, el cliente y el repositorio, junto con otro directorio llamado common
para almacenar los métodos y clases comunes.

