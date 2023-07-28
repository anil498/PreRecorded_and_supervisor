export class PromiseWithTimeout<T> {

    private timeoutHandle: NodeJS.Timeout;
    private timeoutPromise;

    constructor(private promise: () => Promise<T>, private timeoutMs: number, private error: any, private onErrorCleanUpMethod?: () => void) {
        this.timeoutPromise = new Promise<never>((resolve, reject) => {
            const failureObject = (typeof this.error === 'string' || this.error instanceof String) ? new Error(this.error.toString()) : this.error;
            this.timeoutHandle = setTimeout(() => {
                onErrorCleanUpMethod();
                reject(failureObject);
            }, this.timeoutMs);
        });
    }

    public run(): Promise<any> {
        return Promise.race([
            this.promise(),
            this.timeoutPromise,
        ]).then((result) => {
            clearTimeout(this.timeoutHandle);
            return result;
        });
    }

}