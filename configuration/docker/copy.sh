sudo docker cp ./extension/custom-model-context.xml alfresco:/alfresco/tomcat/shared/classes/alfresco/extension
sudo docker cp ./extension/scModel.xml alfresco:/alfresco/tomcat/shared/classes/alfresco/extension
sudo docker cp ./extension/spring-beans.dtd alfresco:/alfresco/tomcat/shared/classes/alfresco/extension

sudo docker cp ./web-extension/share-config-custom.xml alfresco:/alfresco/tomcat/webapps/share/WEB-INF/classes/alfresco/web-extension
sudo docker cp ./web-extension/webclient.properties alfresco:/alfresco/tomcat/webapps/share/WEB-INF/classes/alfresco/web-extension
