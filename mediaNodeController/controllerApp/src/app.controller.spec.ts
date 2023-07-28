import { Test, TestingModule } from '@nestjs/testing';
import { DockerController } from './controllers/docker.controller.js';
import { DockerService } from './services/docker.service.js';

describe('AppController', () => {
  let app: TestingModule;

  beforeAll(async () => {
    app = await Test.createTestingModule({
      controllers: [DockerController],
      providers: [DockerService],
    }).compile();
  });

  describe('getHello', () => {
    it('should return "Hello World!"', () => {
      const appController = app.get<DockerController>(DockerController);
      // expect(appController.getHello()).toBe('Hello World!');
    });
  });
});
