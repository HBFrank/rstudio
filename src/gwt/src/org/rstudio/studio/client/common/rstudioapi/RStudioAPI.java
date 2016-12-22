/*
 * RStudioAPI.java
 *
 * Copyright (C) 2009-16 by RStudio, Inc.
 *
 * Unless you have received this program directly from RStudio pursuant
 * to the terms of a commercial license agreement with RStudio, then
 * this program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */

package org.rstudio.studio.client.common.rstudioapi;

import org.rstudio.core.client.widget.HyperlinkLabel;
import org.rstudio.core.client.widget.MessageDialog;
import org.rstudio.core.client.widget.Operation;
import org.rstudio.studio.client.RStudioGinjector;
import org.rstudio.studio.client.application.events.EventBus;
import org.rstudio.studio.client.common.GlobalDisplay;
import org.rstudio.studio.client.common.rstudioapi.events.RStudioAPIShowDialogEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class RStudioAPI implements RStudioAPIShowDialogEvent.Handler
{
   public RStudioAPI()
   {
   }

   @Inject
   private void initialize(EventBus events,
                           GlobalDisplay globalDisplay)
   {
      events_ = events;
      globalDisplay_ = globalDisplay;

      events_.addHandler(RStudioAPIShowDialogEvent.TYPE, this);
   }
   
   public interface Styles extends CssResource
   {
      String textInfoWidget();
      String installLink();
   }
   
   public interface Resources extends ClientBundle
   {
      @Source("RStudioAPI.css")
      Styles styles();
   }
   
   public static Resources RES = GWT.create(Resources.class);
   public static void ensureStylesInjected() 
   {
      RES.styles().ensureInjected();
   }

   private static void showDialog(String caption, 
                                  String message, 
                                  final String url)
   {
      VerticalPanel verticalPanel = new VerticalPanel();
      verticalPanel.addStyleName(RES.styles().textInfoWidget());

      HTML msg = new HTML(message);
      msg.setWidth("100%");
      verticalPanel.add(msg);
      
      if (url != null)
      {
         HyperlinkLabel link = new HyperlinkLabel(url, 
               new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
               RStudioGinjector.INSTANCE.getGlobalDisplay()
               .openWindow(url);
            }

         });
         link.addStyleName(RES.styles().installLink());
         verticalPanel.add(link);
      }
      
      MessageDialog dlg = new MessageDialog(MessageDialog.INFO,
            caption,
            verticalPanel);

      dlg.addButton("OK", (Operation)null, true, false);
      dlg.showModal();
   }

   @Override
   public void onRStudioAPIShowDialogEvent(RStudioAPIShowDialogEvent event)
   {
      showDialog(
         event.getTitle(), 
         event.getMessage(),
         null);
   }

   private EventBus events_;
   private GlobalDisplay globalDisplay_;
}
