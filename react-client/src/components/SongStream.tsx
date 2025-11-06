import { useState, useEffect} from "react";
import {apiService} from "../services/api.ts";
import {AudioPlayer} from "./AudioPlayer.tsx";
import {useAuth} from "../auth/AuthContext.tsx";

interface SongStreamProps {
    songId: string | null;
}

export const SongStream: React.FC<SongStreamProps> = ({songId}) => {
    const [streamUrl, setStreamUrl] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<String | null>(null);
    const { isAuthenticated, login } = useAuth();

    useEffect(() => {
        const fetchStreamUrl = async () => {
            if (!songId){
                setStreamUrl(null);
                return
            }

            if (!isAuthenticated){
                setError('You must be logged in to stream music.');
                return;
            }

            setIsLoading(true);
            setError(null);
            try {
                const streamUrl = await apiService.getStreamUrl(songId);
                setStreamUrl(streamUrl);
            } catch (error) {
                setError('Failed to load stream');
                setStreamUrl(null);
            } finally {
                setIsLoading(false);
            }
        };

        fetchStreamUrl();
    }, [songId, isAuthenticated]);

    if (songId && !isAuthenticated) {
        return (
            <div className={"w-full max-w-md mx-auto bg-white border border-gray-200 rounded-xl p-6 text-center"}>
                <div className={"flex flex-col items-center space-y-4"}>
                    <svg className={"w-12 h-12 text-gray-400"} fill={"none"} stroke={"currentColor"} viewBox={"0 0 24 24"}>
                        <path strokeLinecap={"round"} strokeLinejoin={"round"} strokeWidth={1} d={"M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"}/>
                    </svg>
                    <div>
                        <h3 className={"text-lg font-semibold text-gray-800 mb-2"}>
                            Sign in required
                        </h3>
                        <p className={"text-lg font-semibold text-gray-800 mb-2"}>
                            Please sign in to stream music.
                        </p>
                    </div>
                    <button
                        onClick={login}
                        className={"bg-green-600 text-white px-6 py-2 rounded-lg hover:bg-green-700 transition duration-200 font-medium"}>
                        Sign in
                    </button>
                </div>
            </div>
        );
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