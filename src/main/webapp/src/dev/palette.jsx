import {
    Category,
    Component,
    Variant,
    Palette,
} from '@react-buddy/ide-toolbox';
import MUIPalette from "@react-buddy/palette-mui";

export const PaletteTree = () => (
        <Palette>
            <Category name="HTML">
                <Component name="a">
                    <Variant requiredParams={['href']}>
                        <a>Link</a>
                    </Variant>
                </Component>
                <Component name="button">
                    <Variant>
                        <button>Button</button>
                    </Variant>
                </Component>
            </Category>
            <MUIPalette/>
        </Palette>
);
