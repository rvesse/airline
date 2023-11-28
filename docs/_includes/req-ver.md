<p markdown="1" class="message">
    <i class="fa fa-exclamation-triangle fa-pull-left"></i> This feature requires a minimum version of {{ include.version }}
    {% if include.module %}
    <br />
    This feature requires the `{{ include.module }}` module
    {% endif %}
 </p>