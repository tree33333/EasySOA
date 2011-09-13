$(function() {

	window.socket = null;
	
	window.AppView = Backbone.View.extend({
	
	    error: function(msg) {
              console.log(msg);
              SubmitForm.failure(msg);
	    },
	    
		/**
		 * Application initialization
		 */
		initialize: function() {
		
    		// Get user name
		    jQuery.ajax({
		        url: '/userdata',
		        success: function(data, textStatus, jqXHR) {
		            var data = jQuery.parseJSON(jqXHR.responseText);
                    $("#username").html(data.username);
                    $("#headerUserBar").show();
                }
            });
		
			this.descriptorsView = new DescriptorsView;
			this.frameView = new FrameView;
			this.navBarView = new NavbarView;
			this.submitFormView = new SubmitFormView;
        
	        socket = io.connect();
	        
            socket.on('error', function(data) {
                  this.error(data.substring(7, data.length-1));
            });
            socket.on('wsdl', function(data) {
                  try {
                      data = jQuery.parseJSON(data);
                      if (!Descriptors.contains(data)) {
                        Descriptors.add(data);
                      }
                  }
                  catch (error) {
                    this.error("Error while handling 'wsdl' message: "+error);
                  }
            });
           /* socket.on('upload', function(data) {
                  try {
                      if (data == 'ok') {
                        SubmitForm.success();
                      }
                      else {
                        SubmitForm.failure(data);
                      }
                  }
                  catch (error) {
                    this.error("Error while handling 'upload' message: "+error);
                  }
            });*/
            socket.on('ready', function(data) {
                  try {
                    $('#messageSuccess').html('Ready.');
                    $('#message').fadeOut(200, function() {
                      $('#messageSuccess').fadeIn(500, function() {
                          setTimeout(function() {
                              $('#messageSuccess').fadeOut(500);
                          }, 1000);
                      });
                    });
                  }
                  catch (error) {
                    this.error("Error while handling 'ready' message: "+error);
                  }
            });
             
         }
         
	});
	
	window.App = new AppView;
	
});