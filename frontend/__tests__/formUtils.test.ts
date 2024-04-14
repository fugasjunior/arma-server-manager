import {getValueByKeyPath} from "../src/util/formUtils";

describe('getValueByKeyPath', () => {
    interface MyInterface {
        key1: boolean;
        key2: number;
        nested: {
            nestedKey: string;
        };
    }

    const myObject: MyInterface = {
        key1: true,
        key2: 42,
        nested: {
            nestedKey: 'example',
        },
    };

    it('should return the correct value for a valid key path', () => {
        expect(getValueByKeyPath(myObject, 'key1')).toBe(true);
        expect(getValueByKeyPath(myObject, 'key2')).toBe(42);
        expect(getValueByKeyPath(myObject, 'nested.nestedKey')).toBe('example');
    });

    it('should return undefined for an invalid key path', () => {
        expect(getValueByKeyPath(myObject, 'nonexistent')).toBeUndefined();
        expect(getValueByKeyPath(myObject, 'nested.nonexistentKey')).toBeUndefined();
    });
});