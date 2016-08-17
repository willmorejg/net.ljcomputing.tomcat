/**
           Copyright 2016, James G. Willmore

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package net.ljcomputing.tomcat.configuration;

import java.io.File;

import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.tomcat.util.descriptor.web.ErrorPage;

/**
 * Tomcat configuration class.
 * 
 * @author James G. Willmore
 *
 */
public class TomcatConfiguration {

  /** The default server port. */
  private static final int DEFAULT_SERVER_PORT = 8080;

  /** The default web app directory. */
  private static final String DEFAULT_WEBAPP_DIR = "src/main/webapp";

  /** The default classes directory. */
  private static final String DEFAULT_CLASSES_DIR = "target/classes";

  /** The default WEB-INF classes directory. */
  private static final String DEFAULT_WEBINF_CLASSES_DIR = "/WEB-INF/classes";

  /** The default ROOT context path. */
  private static final String DEFAULT_ROOT_CONTEXT_PATH = "/";

  /** The server port. */
  private int serverPort = DEFAULT_SERVER_PORT;

  /** The web app directory. */
  private String webAppDirectory = DEFAULT_WEBAPP_DIR;

  /**
   * Gets the web app directory.
   *
   * @return the web app directory
   */
  public String getWebAppDirectory() {
    return webAppDirectory;
  }

  /**
   * Gets the web app directory path.
   *
   * @return the web app dir path
   */
  public String getWebAppDirPath() {
    return new File(webAppDirectory).getAbsolutePath();
  }

  /**
   * Sets the web app directory.
   *
   * @param webAppDirectory the new web app directory
   */
  public void setWebAppDirectory(final String webAppDirectory) {
    this.webAppDirectory = webAppDirectory;
  }

  /**
   * Gets the server port.
   *
   * @return the server port
   */
  public int getServerPort() {
    return serverPort;
  }

  /**
   * Sets the server port.
   *
   * @param serverPort the new server port
   */
  public void setServerPort(final int serverPort) {
    this.serverPort = serverPort;
  }

  /**
   * Create a Tomcat standard context.
   *
   * @param tomcat the Tomcat instance
   * @param contextPath the context path
   * @return the standard context
   * @throws ServletException the servlet exception
   */
  private StandardContext standardContext(final Tomcat tomcat,
      final String contextPath) throws ServletException {
    return (StandardContext) tomcat.addWebapp(contextPath, getWebAppDirPath());
  }
  
  /**
   * Adds the page not found resource.
   *
   * @param ctx the ctx
   */
  private static void addPageNotFound(Context ctx) {
    ErrorPage errorPage = new ErrorPage();
    errorPage.setErrorCode(404);
    errorPage.setLocation("/static/404.htm");
    
    ctx.addErrorPage(errorPage);
  }
  
  /**
   * Adds the internal error resource.
   *
   * @param ctx the ctx
   */
  private static void addInternalError(Context ctx) {
    ErrorPage errorPage = new ErrorPage();
    errorPage.setErrorCode(500);
    errorPage.setLocation("/static/500.htm");
    
    ctx.addErrorPage(errorPage);
  }

  /**
   * New instance of Tomcat configured with configuration settings.
   *
   * @return the tomcat
   * @throws ServletException the servlet exception
   */
  public Tomcat newInstance() throws ServletException {
    final Tomcat tomcat = new Tomcat();
    tomcat.setPort(serverPort);

    final StandardContext ctx = standardContext(tomcat,
        DEFAULT_ROOT_CONTEXT_PATH);

    final File webInfClasses = new File(DEFAULT_CLASSES_DIR);
    final WebResourceRoot resources = new StandardRoot(ctx);
    
    resources.addPreResources(new DirResourceSet(resources,
        DEFAULT_WEBINF_CLASSES_DIR, webInfClasses.getAbsolutePath(),
        DEFAULT_ROOT_CONTEXT_PATH));
    
    ctx.setResources(resources);
    ctx.addWelcomeFile("/pages/index.jsp");
    
    addPageNotFound(ctx);
    addInternalError(ctx);

    return tomcat;
  }
}
