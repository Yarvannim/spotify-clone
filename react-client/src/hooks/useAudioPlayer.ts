import { useState, useRef, useCallback, useEffect } from 'react';

export const useAudioPlayer = () => {
    const [isPlaying, setIsPlaying] = useState(false);
    const [currentTime, setCurrentTime] = useState(0);
    const [duration, setDuration] = useState(0);
    const [currentSongId, setCurrentSongId] = useState<string | null>(null);
    const audioRef = useRef<HTMLAudioElement | null>(null);

    const play = useCallback(async (songId: string, streamUrl: string) => {
        if (audioRef.current) {
            audioRef.current.pause()
        }

        const audio = new Audio(streamUrl);
        audioRef.current = audio;

        audio.addEventListener('loadedmetadata', () => {
            setDuration(audio.duration);
        });

        audio.addEventListener('timeupdate', () => {
            setCurrentTime(audio.currentTime);
        });

        audio.addEventListener('ended', () => {
            setIsPlaying(false);
            setCurrentSongId(null)
        });

        try {
            await audio.play();
            setIsPlaying(true);
            setCurrentSongId(songId);
        } catch (error) {
            console.error('Error playing audio:', error);
        }

    }, []);

    const pause = useCallback(() => {
        if (audioRef.current) {
            try {
                audioRef.current.pause();
                setIsPlaying(false);
            }catch (error) {
                console.error('Error pausing audio:', error);
            }
        }
    }, []);

    const resume = useCallback(async () => {
        if (audioRef.current) {
            try {
                await audioRef.current.play();
                setIsPlaying(true);
            } catch (error) {
                console.error('Error resuming audio:', error);
            }
        }
    }, []);

    const seek = useCallback((time: number) => {
        if (audioRef.current) {
            try {
                audioRef.current.currentTime = time;
            } catch (error) {
                console.error('Error seeking audio:', error);
            }
        }
    }, []);

    useEffect(() => {
        return () => {
            if (audioRef.current) {
                audioRef.current.pause();
            }
        }
    }, []);

    return {
        isPlaying,
        currentTime,
        duration,
        currentSongId,
        play,
        pause,
        resume,
        seek,
    };
};
