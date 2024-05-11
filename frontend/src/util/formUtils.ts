export function getValueByKeyPath<T, K extends keyof T>(obj: T, path: string): T[K] | undefined {
    return path.split('.').reduce<T[K] | undefined>((currentObj, key) => {
        if (typeof currentObj === 'object' && currentObj !== null && key in currentObj) {
            return (currentObj as any)[key];
        }
        return undefined;
    }, obj as T[K]);
}