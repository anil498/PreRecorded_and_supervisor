import { NestFactory } from '@nestjs/core';
import { NestExpressApplication } from '@nestjs/platform-express';
import { AppModule } from './app.module.js';

async function bootstrap() {
  const app = await NestFactory.create<NestExpressApplication>(AppModule);
  const recordingsSystemPath = process.env['MEDIA_NODE_CONTROLLER_RECORDINGS_PATH'] || '/opt/openvidu/recordings';
  // Serve recording files at http://<MEDIA_NODE_IP>/media-node/recordings/<RECORDING_ID>/<FILENAME>.<EXT>
  app.useStaticAssets(recordingsSystemPath, { prefix: '/media-node/recordings', dotfiles: 'allow' });
  await app.listen(3000);
}
bootstrap();
