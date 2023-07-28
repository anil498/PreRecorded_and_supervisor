import { Injectable, OnModuleInit } from '@nestjs/common';
import osUtils from 'os-utils';

@Injectable()
export class CpuService implements OnModuleInit {

    // Default values will store the last 40 cpu loads in 3 second intervals (2 minutes in the past)
    private CPU_INTERVAL_SECONDS: number = Number(process.env['OPENVIDU_PRO_CLUSTER_LOAD_INTERVAL']) || 3;
    private MAX_CPU_ENTRIES_RECORDED = Number(process.env['OPENVIDU_PRO_CLUSTER_LOAD_REGISTRIES']) || 40;

    private lastCpuLoads: number[] = [];

    onModuleInit() {
        const cpuCollection = () => {
            osUtils.cpuUsage(cpuLoad => {
                if (this.lastCpuLoads.length >= this.MAX_CPU_ENTRIES_RECORDED) {
                    this.lastCpuLoads.pop();
                }
                this.lastCpuLoads.unshift(cpuLoad);
            });
        }
        cpuCollection();
        setInterval(cpuCollection, this.CPU_INTERVAL_SECONDS * 1000);
    }

    /**
     * Get the current CPU load of the system (number between 0.0 to 100.0)
     * 
     * @param avgFromLastSeconds How many seconds back in time should be taken into account to calculate the average CPU load
     * By default the load returned is the average of the registries of the last 9 seconds (3 registries by default)
     */
    public getCpuLoad(avgFromLastSeconds = 9): number {
        const registriesToTake = Math.min(Math.ceil(avgFromLastSeconds / this.CPU_INTERVAL_SECONDS), this.MAX_CPU_ENTRIES_RECORDED);
        return (this.average(this.lastCpuLoads.slice(0, registriesToTake))) * 100;
    }

    private average = arr => arr.reduce((a, b) => a + b, 0) / arr.length;

}