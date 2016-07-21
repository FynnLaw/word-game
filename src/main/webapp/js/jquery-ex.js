(function() {
	$.extend($.fn, {
		mask : function(msg, maskDivClass) {
			this.unmask();
			var op = {
				opacity : 0.6,
				z : 10000,
				bgcolor : '#ccc'
			};
			var original = $(document.body);
			if (this[0] && this[0] !== window.document) {
				original = $(this[0]);
			}

			var maskDiv = $('<div class="maskdivgen">&nbsp;</div>');
			maskDiv.appendTo(original);

			
			if (!/^body/i.test(original.get(0).tagName) && original.css('position') == 'static') {
                original.attr("oldPosition",original.css('position'));
                original.css('position','relative');
            }
			
			maskDiv.css({
						position : 'absolute',
						top : '0',
						left : '0',
						'z-index' : op.z,
						width : '100%',
						height : '100%',
						'background-color' : op.bgcolor,
						opacity : 0
					});
			if (maskDivClass) {
				maskDiv.addClass(maskDivClass);
			}
			if (msg) {
				var msgDiv = $('<div style="position:absolute;border:#6593cf 1px solid; padding:2px;background:#ccca"><div style="line-height:24px;border:#a3bad9 1px solid;background:white;padding:2px 10px 2px 10px">'
						+ msg + '</div></div>');
				msgDiv.appendTo(maskDiv);

				var widthspace = (maskDiv.width() - msgDiv.width());
				var heightspace = (maskDiv.height() - msgDiv.height());
				msgDiv.css({
							cursor : 'wait',
							top : (heightspace / 2 - 2),
							left : (widthspace / 2 - 2)
						});
			}
			maskDiv.fadeIn('fast', function() {
						$(this).fadeTo('slow', op.opacity);
					})
			return maskDiv;
		},
		unmask : function() {
			var original = $(document.body);
			if (this[0] && this[0] !== window.document) {
				original = $(this[0]);
			}
			
			if(original.attr("oldPosition")) {
				original.css('position',original.attr("oldPosition"));
				original.removeAttr("oldPosition");
			}
			
			original.find("> div.maskdivgen").fadeOut('slow', 0, function() {
						$(this).remove();
					});
		}
	});
})();
