import CodeMirror from '@uiw/react-codemirror';
import {json, jsonParseLinter} from '@codemirror/lang-json';
import {cpp} from '@codemirror/lang-cpp';
import {EditorView} from '@codemirror/view';
import {linter, lintGutter} from '@codemirror/lint';
import {useTheme} from '@mui/material';

const extensionsFor = (language: 'json' | 'cpp') =>
    language === 'json'
        ? [json(), linter(jsonParseLinter()), lintGutter(), EditorView.lineWrapping]
        : [cpp(), EditorView.lineWrapping];

type Props = {
    language: 'json' | 'cpp';
    value: string;
    onChange: (value: string) => void;
};

export default function RawConfigEditor({language, value, onChange}: Props) {
    const theme = useTheme();
    return (
        <CodeMirror
            value={value}
            onChange={onChange}
            extensions={extensionsFor(language)}
            theme={theme.palette.mode}
            minHeight="200px"
            maxHeight="500px"
        />
    );
}
