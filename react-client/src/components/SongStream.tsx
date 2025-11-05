import { useState, useCallback, useEffect} from "react";
import {apiService} from "../services/api.ts";
import {AudioPlayer} from "./AudioPlayer.tsx";

interface SongStreamProps {
    songId: string | null;
}

export const SongStream: React.FC<SongStreamProps> = ({songId}) => {
    const [streamUrl, setStreamUrl] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<String | null>(null);

    const loadStreamUrl = useCallback(async (id: string) => {
        setIsLoading(true);
        setError(null);
        try {
            const url = await apiService.getStreamUrl(id);
            setStreamUrl(url);
        } catch (error) {
            setError((error as Error).message);
            setStreamUrl(null);
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        if (songId){
            loadStreamUrl(songId);
        } else {
            setStreamUrl(null);
            setError(null);
        }
    }, [songId, loadStreamUrl]);

    if (!songId) {
        return null;
    }

    return (
        <div className={"w-full"}>
            {isLoading && (
                <div className={"flex items-center justify-center space-x-2 text-gray-600 mb-4"}>
                    <div className={"animate-spin rounded-full h-4 w-4 border-b-2 border-green-500"}>
                        <span>Loading...</span>
                    </div>
                </div>
            )}
            {error && (
                <div className={"p-4 bg-red-50 border border-red-200 rounded-lg text-red-700 text-center mb-4"}>
                    Error: {error}
                </div>
            )}
            <AudioPlayer songId={songId} streamUrl={streamUrl}/>
        </div>
    )
}