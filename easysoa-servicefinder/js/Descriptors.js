$(function() {	/**	 * Model of a single found descriptor	 */	window.Descriptor = Backbone.Model.extend({			/**		 * Attributes		 * name: WSDL name (link title)		 * url: WSDL url (link ref)		 * (attributes set by NavbarView.render())		 */				initialize: function() {}			});	/**	 * View of a single descriptor	 */	window.DescriptorView = Backbone.View.extend({			tagName: "li",			template: _.template($('#descriptorEntry').html()),				events: {			"click input": "select"		},			initialize: function() {			_.bindAll(this, 'render', 'select');			this.model.view = this;			this.model.bind('change', this.render);		},				render: function() {			$(this.el).html(this.template(this.model.toJSON()));			return this;		},				select: function() {			SubmitForm.select(this.model.toJSON());		}			});		/**	 * Descriptor collection	 */	window.DescriptorList = Backbone.Collection.extend({			model: Descriptor,				initialize: function() {}			});		window.Descriptors = new DescriptorList;		/**	 * Found descriptors global view	 */	window.DescriptorsView = Backbone.View.extend({			el: $("#foundList"),			template: _.template($('#descriptorEntry').html()),			initialize: function() {			_.bindAll(this, 'addOne');			Descriptors.view = this;			Descriptors.bind('add', this.addOne);		},				addOne: function(descriptor) {			var view = new DescriptorView({model: descriptor});			this.el.append(view.render().el);		}			});	});