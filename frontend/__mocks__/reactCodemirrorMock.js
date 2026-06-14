const React = require('react');

function CodeMirrorMock({value, onChange}) {
    return React.createElement('textarea', {
        value: value ?? '',
        onChange: (e) => onChange?.(e.target.value),
        readOnly: !onChange,
    });
}

module.exports = CodeMirrorMock;
module.exports.default = CodeMirrorMock;
