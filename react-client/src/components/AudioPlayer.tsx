import {useCallback} from "react";
import {useAudioPlayer} from "../hooks/useAudioPlayer.ts";

interface AudioPlayerProps {
    songId: string | null;
    streamUrl: string | null;
}

export const AudioPlayer: React.FC<AudioPlayerProps> = ({songId, streamUrl}) => {
    const {isPlaying, currentTime, duration, currentSongId, play, pause, resume, seek} = useAudioPlayer();

    const handlePlayPause = useCallback(() => {
        if (!songId || !streamUrl) return;
        if (currentSongId === songId) {
            if (isPlaying) {
                pause();
            } else {
                resume();
            }
        } else {
            play(songId, streamUrl);
        }
    }, [songId, streamUrl, currentSongId, isPlaying, pause, play, resume]);

    const handleSeek = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
        const time = parseFloat(e.target.value);
        seek(time);
    }, [seek]);

    const formatTime = (time: number) => {
        const minutes = Math.floor(time / 60);
        const seconds = Math.floor(time % 60);
        return `${minutes}:${seconds.toString().padStart(2, '0')}`;
    };

    if (!songId || !streamUrl) {
        return (
            <div
                className="w-full max-w-md mx-auto bg-gray-50 border border-gray-200 rounded-xl p-6 text-center text-gray-500">
                <div className="flex items-center justify-center space-x-2">
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                              d="M9 19V6l12-3v13M9 19c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zm12-3c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2z"/>
                    </svg>
                    <span>Select a song to play</span>
                </div>
            </div>
        );
    }

    return (
        <div className="w-full max-w-md mx-auto bg-white border border-gray-200 rounded-xl p-6 shadow-lg">
            <div className="flex flex-col space-y-4">
                <button
                    onClick={handlePlayPause}
                    className="flex items-center justify-center w-12 h-12 bg-green-500 hover:bg-green-600 text-white rounded-full transition-all duration-200 focus:outline-none focus:ring-4 focus:ring-green-200 mx-auto"
                    disabled={!streamUrl}
                >
                    {isPlaying && currentSongId === songId ? (
                        <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
                            <path d="M6 4h4v16H6zM14 4h4v16h-4z"/>
                        </svg>
                    ) : (
                        <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
                            <path d="M8 5v14l11-7z"/>
                        </svg>
                    )}
                </button>

                <div className="flex items-center space-x-3">
                  <span className="text-sm text-gray-500 font-mono min-w-[40px]">
                    {formatTime(currentTime)}
                  </span>
                    <input
                        type="range"
                        min="0"
                        max={duration || 0}
                        value={currentTime}
                        onChange={handleSeek}
                        className="flex-1 h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer [&::-webkit-slider-thumb]:appearance-none [&::-webkit-slider-thumb]:h-4 [&::-webkit-slider-thumb]:w-4 [&::-webkit-slider-thumb]:rounded-full [&::-webkit-slider-thumb]:bg-green-500 hover:[&::-webkit-slider-thumb]:bg-green-600"
                    />
                    <span className="text-sm text-gray-500 font-mono min-w-[40px]">{
                        formatTime(duration)}
                    </span>
                </div>
            </div>
        </div>
    );
};