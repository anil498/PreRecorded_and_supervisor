import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { DockerController } from './controllers/docker.controller.js';
import { DockerService } from './services/docker.service.js';
import { APP_GUARD } from '@nestjs/core';
import { AuthGuard } from './auth.guard.js';
import { SystemService } from './services/system.service.js';
import { CpuService } from './services/cpu.service.js';
import { ConfigController } from './controllers/config.controller.js';
import globalConfiguration from '../config/global.configuration.js';
import { MediaNodeControllerConfigService } from './services/media-node-controller-config.service.js';
import { ScheduleModule } from "@nestjs/schedule";
import { LogCleanerService } from './services/log-cleaner.service.js';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      load: [globalConfiguration]
    }),
    ScheduleModule.forRoot()],
  controllers: [
    DockerController,
    ConfigController
  ],
  providers: [
    DockerService,
    SystemService,
    CpuService,
    MediaNodeControllerConfigService,
    LogCleanerService,
    {
      provide: APP_GUARD,
      useClass: AuthGuard,
    }
  ],
})
export class AppModule { }
