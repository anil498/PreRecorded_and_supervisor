
import { Router } from '@angular/router';
import {ParticipantAbstractModel, ParticipantService, RecordingInfo, TokenModel } from '../../../dist/openvidu-angular';
import { RestService } from '../services/rest.service';
import { animals, colors, Config, countries, names, uniqueNamesGenerator } from 'unique-names-generator';
import { Observable, Subscription, catchError } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component , ElementRef, Inject, OnInit, Output, ViewChild } from '@angular/core';
import { OpenVidu, Publisher, PublisherProperties, Session, StreamEvent } from 'openvidu-browser';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef , MatDialogActions , MatDialogContent } from '@angular/material/dialog';
import { saveAs } from 'file-saver';
import {EventEmitter} from 'events';
import {SharedService} from '../services/shared.service';


@Component({
	selector: 'app-call',
	templateUrl: './call.component.html',
	styleUrls: ['./call.component.scss'],
})
export class CallComponent implements OnInit {

	
	private OV: OpenVidu;
    private session: Session;
    public publisher: Publisher;
	private token: string;
	private playing = false;

	mp4Data: any[];



	sessionId = 'daily-call1';
	tokens: TokenModel;

	joinSessionClicked: boolean = false;
	closeClicked: boolean = false;
	isSessionAlive: boolean = false;
	recordingList: RecordingInfo[] = [];
	recordingError: any;
	broadcastingError: any;

	localParticipant!: ParticipantAbstractModel;
	remoteParticipants!: ParticipantAbstractModel[];
	localParticipantSubs!: Subscription;
	remoteParticipantsSubs!: Subscription;
	elementRef: any;
	renderer: any;
	_stream: any;
	openviduService: any;
	layoutService: any;
	
	


	
	constructor(private http: HttpClient,private restService: RestService,private router:Router, private participantService: ParticipantService,private httpClient: HttpClient, private dialog: MatDialog ,private sharedService: SharedService ) {
	}

	
	


	async ngOnInit() {
		await this.requestForTokens();
		
	}

	async onNodeCrashed() {
		// Request the tokens again for reconnect to the session
		await this.requestForTokens();
	}


	onJoinClicked() {
		console.warn('VC JOIN BUTTON CLICKED');
	}

	onToolbarCameraButtonClicked() {
		console.warn('VC camera CLICKED');
	}
	onToolbarMicrophoneButtonClicked() {
		// console.warn("my token is --->"+this.openvidu.session.token;
		console.warn('VC microphone CLICKED');

	}
	onToolbarScreenshareButtonClicked() {
		console.warn('VC screenshare CLICKED');
	}
	onToolbarFullscreenButtonClicked() {
		console.warn('VC fullscreen CLICKED');
	}
	onToolbarParticipantsPanelButtonClicked() {
		console.warn('VC participants CLICKED');
	}
	onToolbarChatPanelButtonClicked() {
		console.warn('VC chat CLICKED');
	}

	onToolbarLeaveButtonClicked() {
		this.isSessionAlive = false;
		console.log('VC LEAVE BUTTON CLICKED');
		this.router.navigate([`/customer`]);
	}

	onCameraButtonClicked() {
		console.warn('TOOLBAR camera CLICKED');
	}
	onMicrophoneButtonClicked() {
		console.warn('TOOLBAR microphone CLICKED');
	}
	onScreenshareButtonClicked() {
		console.warn('TOOLBAR screenshare CLICKED');
	}
	onFullscreenButtonClicked() {
		console.warn('TOOLBAR fullscreen CLICKED');
	}
	onParticipantsPanelButtonClicked() {
		console.warn('TOOLBAR participants CLICKED');
	}
	onChatPanelButtonClicked() {
		console.warn('TOOLBAR chat CLICKED');
	}

	onLeaveButtonClicked() {
		this.isSessionAlive = false;
		console.log('TOOLBAR LEAVE CLICKED');
	}

	async onStartBroadcastingClicked(broadcastUrl: string) {
		console.log('START STREAMING', broadcastUrl);
		try {
			this.broadcastingError = null;
			const resp = await this.restService.startBroadcasting(broadcastUrl);
			console.log('Broadcasting response ', resp);
		} catch (error) {
			console.error(error);
			this.broadcastingError = error.error;
		}
	}

	async onStopBroadcastingClicked() {
		console.log('STOP STREAMING');
		try {
			this.broadcastingError = null;
			const resp = await this.restService.stopBroadcasting();
			console.log('Broadcasting response ', resp);
		} catch (error) {
			console.error(error);
			this.broadcastingError = error.message || error;
		}
	}

	async onStartRecordingClicked() {
		console.warn('START RECORDING CLICKED');
		try {
			await this.restService.startRecording(this.sessionId);
		} catch (error) {
			this.recordingError = error;
		}
	}
	async onStopRecordingClicked() {
		console.warn('STOP RECORDING CLICKED');
		try {
			this.recordingList = await this.restService.stopRecording(this.sessionId);
			console.log('RECORDING LIST ', this.recordingList);
		} catch (error) {
			this.recordingError = error;
		}
	}

	async onDeleteRecordingClicked(recordingId: string) {
		console.warn('DELETE RECORDING CLICKED');

		try {
			this.recordingList = await this.restService.deleteRecording(recordingId);
		} catch (error) {
			this.recordingError = error;
		}
	}

	private async requestForTokens() {
		const response = await this.restService.getTokensFromBackend(this.sessionId);
		this.recordingList = response.recordings;
		this.tokens = {
			webcam: response.cameraToken,
			screen: response.screenToken
		};

		console.log('Token requested: ', this.tokens);
		return this.tokens;
	}
	private getRandomName(): string {
		const configName: Config = {
			dictionaries: [names, countries, colors, animals],
			separator: '-',
			style: 'lowerCase'
		};
		return uniqueNamesGenerator(configName).replace(/[^\w-]/g, '');
	}

	subscribeToParticipants() {
		this.localParticipantSubs = this.participantService.localParticipantObs.subscribe((p) => {
		  this.localParticipant = p;
		});
	  
		this.remoteParticipantsSubs = this.participantService.remoteParticipantsObs.subscribe((participants) => {
		  this.remoteParticipants = participants;
		});
	  }

	



  public downloadFile(url: string, fileName: string): void {
this.http.get(url, { responseType: 'blob' })
  .subscribe(response => {
	const blob = new Blob([response], { type: 'video/mp4' });
	saveAs(blob,fileName);
	console.log("Video Saved")
  });
 }

// async playvideobutton(){

// 	console.log("save button works");
// 	this.downloadFile('http://172.17.0.122:5000/download/1.mp4','myvideo.mp4');
// 	// this.downloadFile('http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4','myvideo.mp4');


// 	console.log("Play Button Works");

// 	    // Initialize OpenVidu
//     const OV = new OpenVidu();

//     // Initialize session
//     const session = OV.initSession();

//     // Create a custom video element with the video and audio streams
//     const videoElement = document.createElement('video');
//     videoElement.src = 'assets/video/music.mp4';
//     videoElement.controls = true;
// 	videoElement.setAttribute('controls', true.toString()); // or use setAttribute
		

//     // Wait for video to be loaded and start playing
//     videoElement.onloadedmetadata = async () => {
//         videoElement.play();

//         // Create an AudioContext
//         const audioContext = new AudioContext();

//         // Create a MediaElementSourceNode to connect the videoElement to the AudioContext
//         const sourceNode = audioContext.createMediaElementSource(videoElement);

//         // Create a MediaStreamDestination to capture the audio from the AudioContext
//         const audioDestination = audioContext.createMediaStreamDestination();
//         sourceNode.connect(audioDestination);

//         // Create a video stream from the canvas
//         const canvas = document.createElement('canvas');
//         const videoWidth = videoElement.videoWidth;
//         const videoHeight = videoElement.videoHeight;
//         canvas.width = videoWidth;
//         canvas.height = videoHeight;
//         const canvasContext = canvas.getContext('2d');
//         const videoStream = canvas.captureStream();
//         const audioTrack = audioDestination.stream.getAudioTracks()[0];

//         console.log("Audio is : "+audioTrack);

//         // Initialize publisher
//         const publisher = OV.initPublisher(
//             undefined,
//             {
//                 audioSource: audioTrack,
//                 videoSource: videoStream.getVideoTracks()[0],
//                 publishAudio: true,
//                 publishVideo: true,
//                 mirror: false,
				
//             }
//         );


//         // Draw each frame of the video to the canvas and capture it as an image
//         const drawFrame = () => {
//             canvasContext.drawImage(videoElement, 0, 0, videoWidth, videoHeight);
//             const imageData = canvas.toDataURL('image/jpeg', 0.8);
//             publisher.publishVideo(false);
//             publisher.publishVideo(true);
//             setTimeout(drawFrame, 1000 / 30);
//         };
//         drawFrame();

//         // Connect to session and publish
//         session.connect(this.tokens.screen).then(() => {
//             session.publish(publisher);
//         });

		
//     };
	
// }


    


	async uploadfile(){
		console.log("Upload button clicked");

		const dialogRef = this.dialog.open(VideoFileDialogComponent, {
			width: '500px',
			height: '500px',
			data: {
			  accept: 'video/*'
			}
		  });

		  dialogRef.afterClosed().subscribe(result => {
			if (result) {
			  console.log('Selected video file:', result);

			}
		  });
	}


	//For Downloading a File from Api

	async savebutton(){
	console.log("save button works");
	this.downloadFile('http://172.17.0.122:5000/download/1.mp4','myvideo.mp4');
	// this.downloadFile('http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4','myvideo.mp4');
  }

	
	private duration: number;
	async playvideobutton(){

		var isPlaying = true;
		var isAudio = true;

		await this.requestForTokens();
		
		console.log("Select button clicked");

	const dialogRef = this.dialog.open(FileDialogComponent, {
		width: '500px',
		height: '500px',
		data: {
		  accept: 'video/*'
		}
	  });

	  dialogRef.afterClosed().subscribe(result => {
		if (result) {
		  console.log('Selected video file:', result);
		 console.log(result);
		}
	  
	
	
		console.log("Play Button Works");
	
			// Initialize OpenVidu
		const OV = new OpenVidu();
	
		// Initialize session
		const session = OV.initSession();
	
		// Create a custom video element with the video and audio streams
		let videoElement = document.createElement('video');
		// videoElement.src = 'assets/video/music.mp4';
		videoElement.src = 'assets/video/'+result.name;
		console.log('assets/video/'+result.name);
		videoElement.controls = true;
		videoElement.setAttribute('controls', true.toString()); // or use setAttribute
			
	
		// Wait for video to be loaded and start playing
		videoElement.onloadedmetadata = async () => {
			videoElement.play();
	
			// Create an AudioContext
			const audioContext = new AudioContext();
	
			// Create a MediaElementSourceNode to connect the videoElement to the AudioContext
			const sourceNode = audioContext.createMediaElementSource(videoElement);
	
			// Create a MediaStreamDestination to capture the audio from the AudioContext
			const audioDestination = audioContext.createMediaStreamDestination();
			sourceNode.connect(audioDestination);
	
			// Create a video stream from the canvas
			let canvas = document.createElement('canvas');
			const videoWidth = videoElement.videoWidth;
			const videoHeight = videoElement.videoHeight;
			canvas.width = videoWidth;
			canvas.height = videoHeight;
			const canvasContext = canvas.getContext('2d');
			const videoStream = canvas.captureStream();
			const audioTrack = audioDestination.stream.getAudioTracks()[0];
			this.duration = videoElement.duration;

			console.log("Audio is : "+audioTrack);

		
			// Initialize publisher
			 this.publisher = OV.initPublisher(
				undefined,
				{
					audioSource: audioTrack,
					videoSource: videoStream.getVideoTracks()[0],
					publishAudio: true,
					publishVideo: true,
					mirror: false,
					frameRate:60,
					
				}
			); 
			
		
		
	
	
			// Draw each frame of the video to the canvas and capture it as an image
			const drawFrame = () => {
				if (isPlaying) {
				  canvasContext.drawImage(videoElement, 0, 0, videoWidth, videoHeight);
				  const imageData = canvas.toDataURL("image/jpeg", 0.8);
				  this.publisher.publishVideo(false);
				  this.publisher.publishVideo(true);
				}
				setTimeout(drawFrame, 1000 / 30);
			  };
			  drawFrame();
	
			// Connect to session and publish
			session.connect(this.tokens.screen).then(() => {
				session.publish(this.publisher);
			});
		
	

			//Pause / Unpause Video

			this.emitter.on('Button Clicked', (message: string) => {
				console.log(`Received message: ${message}`);

				isPlaying = !isPlaying; // toggle isPlaying variable
				if (isPlaying) {
				  videoElement.play(); // resume video playback
				//   publisher.publishVideo(true); // resume publishing stream
				} else {
				  videoElement.pause(); // pause video playback
				//   publisher.publishVideo(false); // pause publishing stream
				}

			  });

			  //Exit Stream 

			  this.emitter.on('Exit Button Clicked', (message: string) => {
				console.log(`Received message: ${message}`);

				session.disconnect();
				session.unpublish(this.publisher);
				canvas=null;
				videoElement=null;
				this.publisher=null;
			
			});

			// Mute/Unmute Audio

			this.emitter.on('Mute Button Clicked', (message: string) => {
				console.log(`Received message: ${message}`);
				
				isAudio = !isAudio; // toggle isAudio variable
				if (isAudio) {
				   // mute/unmute audio
				   this.publisher.publishAudio(true);
				} else {
				   // mute/unmute audio
				   this.publisher.publishAudio(false);
				}
			
			});

			// Play/Pause from Dialog box
			
			  this.sharedService.getPlayPauseData().subscribe(data => {
				// handle the emitted data here

				console.log(data);

				isPlaying = !isPlaying; // toggle isPlaying variable
				if (isPlaying) {
				  videoElement.play(); // resume video playback
				//   publisher.publishVideo(true); // resume publishing stream
				} else {
				  videoElement.pause(); // pause video playback
				//   publisher.publishVideo(false); // pause publishing stream
				}
			
			  });
			

			  // Mute/Unmute Data from Dialog box

			  this.sharedService.getMuteData().subscribe(data => {
				// handle the emitted data here
				
				console.log(data);

					isAudio = !isAudio; // toggle isAudio variable
				if (isAudio) {
				   // mute/unmute audio
				   this.publisher.publishAudio(true);
				} else {
				   // mute/unmute audio
				   this.publisher.publishAudio(false);
				}
				
			});


			// Slider Data from Dialog Box

			this.sharedService.getSlideData().subscribe(data => {

				
				const slider_value =parseInt(data);
				const time = slider_value / 100 * this.duration;
				videoElement.currentTime = time;
				
				drawFrame();
			
			});


			//Exit Button
			this.sharedService.getExitData().subscribe(data => {
				
				console.log(data);
				session.disconnect();
				session.unpublish(this.publisher);
				canvas=null;
				videoElement=null;
				this.publisher=null;
			});
		
		};
	});


}
	
	private emitter = new EventEmitter();

	playPauseButton(){
		this.emitter.emit('Button Clicked', 'Button Clicked');
	}

	exitbutton(){
		this.emitter.emit('Exit Button Clicked', 'Exit Button Clicked');
	}

	mutebutton(){
		this.emitter.emit('Mute Button Clicked', 'Mute Button Clicked');
	
	}

	async opendialog(){
		const dialogRef = this.dialog.open(DialogComponent, {
			
		  });

		  dialogRef.afterClosed().subscribe(result => {
			if (result) {
			  
				console.log(result);
			}
		  });
	}

	
	

}

	



  @Component({
	selector: 'app-video-file-dialog',
	template: `
	<h1 mat-dialog-title>Select Video File</h1>
	<div mat-dialog-content>
	  <input type="file" (change)="onFileSelected($event)">
	</div>
	
	<div mat-dialog-actions>
	  <button mat-button (click)="dialogRef.close()">Cancel</button>
	</div>`
  })
  export class VideoFileDialogComponent {
  
	constructor(
	  public dialogRef: MatDialogRef<VideoFileDialogComponent>,
	  @Inject(MAT_DIALOG_DATA) public data: any , private http: HttpClient
	) { }
  
	onFileSelected(event) {
	  
	  const file = event.target.files[0];

	const fileReader = new FileReader();
	fileReader.readAsBinaryString(file);
	fileReader.onload = () => {
 	const binaryData = fileReader.result;
	const blob = new Blob([binaryData], { type: file.type });
  
	const formData = new FormData();
	formData.append('file', blob, file.name);

	this.http.post('http://172.17.0.122:5000/upload', formData).subscribe(
    (response) => {
      console.log('Upload successful!');
    },
    (error) => {
      console.error('Upload failed!');
    }
  );
	  if (file && file.type.match(this.data.accept)) {
		this.dialogRef.close(file);
	  }
};
	}
  
  }

  
  @Component({
	selector: 'app-dialog',
	template: `
	  <h2 mat-dialog-title>Control Video</h2>
<div class="bottom-content">
  <div mat-dialog-content>
    <button mat-icon-button (click)="onPlay()">
      <mat-icon>
        play_arrow
      </mat-icon>
    </button>
    <button mat-icon-button (click)="onMute()">
      <mat-icon>
        volume_mute
      </mat-icon>
    </button>
    <mat-slider min="1" max="100" step="2" #slider (input)="onSlide($event)">
      <input matSliderThumb>
    </mat-slider>
    <button mat-icon-button (click)="onExit()">
      <mat-icon>
        close
      </mat-icon>
    </button>
  </div>
  <div mat-dialog-actions>
    <button mat-button [mat-dialog-close]="true">Close Panel</button>
  </div>
</div>

	`,
  })
  export class DialogComponent {

	constructor(
	  public dialogRef: MatDialogRef<DialogComponent>,
	  @Inject(MAT_DIALOG_DATA) public data: any , private sharedService: SharedService) {}
  
		
	onPlay(): void {
		console.log("Play/Pause Button CLicked");
		this.sharedService.emitPlayPauseData("Play Button Clicked");
	  }


	onMute(): void {
		console.log("Mute Button CLicked");
		this.sharedService.emitMuteData("Mute Button Clicked");

	  }

	onSlide(event:any): void{
		console.log(event.value);
		this.sharedService.emitSlideData(event.value);
	}

	onExit(): void{
		console.log("Exit Button Clicked");
		this.sharedService.emitExitData("Exit Button Clicked");
	}
  
  }


  @Component({
	selector: 'app-file-dialog',
	template: `     
	<h1 mat-dialog-title>Select Video File</h1>
	<div mat-dialog-content>
	  <input type="file" (change)="onFileSelected($event)">
	</div>
	
	<div mat-dialog-actions>
	  <button mat-button (click)="dialogRef.close()">Cancel</button>
	</div>
	`,
  })
  export class FileDialogComponent  {
	constructor(
		public dialogRef: MatDialogRef<FileDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: any , private http: HttpClient
	  ) { }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    console.log(file); // This will give you the path of the selected file
  

	if (file && file.type.match(this.data.accept)) {
		this.dialogRef.close(file);
	  }
	}

  }
  

 

  //METHOD TO UPLOAD FILE THORUGH API

  // const videoFilePath = this.link;

    // // Read the video file as a blob
    // const xhr = new XMLHttpRequest();
    // xhr.open('GET', videoFilePath, true);
    // xhr.responseType = 'blob';
    // xhr.onload = () => {
    //   const videoBlob = xhr.response;

    //   // POST the video blob to your server
    //   const formData = new FormData();
    //   formData.append('video', videoBlob, 'video.mp4');
    //   this.http.post('http://172.17.0.122:5000', formData).subscribe(
    //     () => console.log('Video saved successfully'),
    //     error => console.error('Error saving video:', error)
    //   );
    // };
    // xhr.send();


	// Working Playvideo Button

// async playvideobutton(){

//     // Initialize OpenVidu
//     const OV = new OpenVidu();

//     // Initialize session
//     const session = OV.initSession();

//     // Create a custom video element with the video and audio streams
//     const videoElement = document.createElement('video');
//     videoElement.src = 'assets/video/music.mp4';
//     videoElement.controls = true;
// 	videoElement.setAttribute('controls', true.toString()); // or use setAttribute
// 	videoElement.style.setProperty('controls', 'true', 'important');	

//     // Wait for video to be loaded and start playing
//     videoElement.onloadedmetadata = async () => {
//         videoElement.play();

//         // Create an AudioContext
//         const audioContext = new AudioContext();

//         // Create a MediaElementSourceNode to connect the videoElement to the AudioContext
//         const sourceNode = audioContext.createMediaElementSource(videoElement);

//         // Create a MediaStreamDestination to capture the audio from the AudioContext
//         const audioDestination = audioContext.createMediaStreamDestination();
//         sourceNode.connect(audioDestination);

//         // Create a video stream from the canvas
//         const canvas = document.createElement('canvas');
//         const videoWidth = videoElement.videoWidth;
//         const videoHeight = videoElement.videoHeight;
//         canvas.width = videoWidth;
//         canvas.height = videoHeight;
//         const canvasContext = canvas.getContext('2d');
//         const videoStream = canvas.captureStream();
//         const audioTrack = audioDestination.stream.getAudioTracks()[0];

//         console.log("Audio is : "+audioTrack);

//         // Initialize publisher
//         const publisher = OV.initPublisher(
//             undefined,
//             {
//                 audioSource: audioTrack,
//                 videoSource: videoStream.getVideoTracks()[0],
//                 publishAudio: true,
//                 publishVideo: true,
//                 mirror: false,
				
//             }
//         );


//         // Draw each frame of the video to the canvas and capture it as an image
//         const drawFrame = () => {
//             canvasContext.drawImage(videoElement, 0, 0, videoWidth, videoHeight);
//             const imageData = canvas.toDataURL('image/jpeg', 0.8);
//             publisher.publishVideo(false);
//             publisher.publishVideo(true);
//             setTimeout(drawFrame, 1000 / 30);
//         };
//         drawFrame();

//         // Connect to session and publish
//         session.connect(this.tokens.screen).then(() => {
//             session.publish(publisher);
//         });

		
//     };
// }


//For Downloading a File from Api

// async savebutton(){
// 	console.log("save button works");
// 	this.downloadFile('http://172.17.0.122:5000/download/1.mp4','myvideo.mp4');
// 	// this.downloadFile('http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4','myvideo.mp4');
//   }


// For Selecting a File from a folder

// Select Video File
// async selectvideofile(){
// 	console.log("Select button clicked");

// 	const dialogRef = this.dialog.open(FileDialogComponent, {
// 		width: '500px',
// 		height: '500px',
// 		data: {
// 		  accept: 'video/*'
// 		}
// 	  });

// 	  dialogRef.afterClosed().subscribe(result => {
// 		if (result) {
// 		  console.log('Selected video file:', result);
// 		 console.log(result);
// 		}
// 	  });
// }
	


  


 
