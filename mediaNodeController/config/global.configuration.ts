export default () => ({
    recordingsSystemPath: process.env['MEDIA_NODE_CONTROLLER_RECORDINGS_PATH'] || '/opt/openvidu/recordings',
    logsDirectories: ['/opt/openvidu/kurento-logs'],
    maxLogFiles: process.env["MAX_LOG_FILES"] || '10'
});
