import React, { useState, useCallback, useDeferredValue, useEffect } from "react";
import { useSongSearch } from "../hooks/useSongSearch.ts";

interface SearchBarProps {
    onSongSelect: (songId: string) => void;
}

export const SearchBar: React.FC<SearchBarProps> = ({onSongSelect}) => {
    const [query, setQuery] = useState('');
    const deferredQuery = useDeferredValue(query);
    const { results, isLoading, error, search } = useSongSearch();

    useEffect(() => {
        search(deferredQuery)
    }, [deferredQuery, search])

    const handleInputChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) =>{
        setQuery(e.target.value)
    }, []);

    const handleSongSelect = useCallback((songId: string) => {
        onSongSelect(songId)
        setQuery('')
    }, [onSongSelect]);

    return (
        <div className={"w-full max-w-2xl mx-auto"}>
            <div className={"relative"}>
                <input
                    type={"text"}
                    value={query}
                    onChange={handleInputChange}
                    placeholder={"Search for a song..."}
                    className={"w-full px-4 py-3 text-lg border-2 border-gray-300 rounded-full focus:outline-none focus:border-green-500 focus:ring-2 focus:ring-green-200 transition-all duration-200"}
                />
                {isLoading && (
                    <div className={"absolute right-4 top-1/2 transform -translate-y-1/2"}>
                        <div className={"animate-spin rounded-full h-6 w-6 border-b-2 border-green-500"}></div>
                    </div>
                )}
            </div>
                {error && (
                    <div className={"mt-2 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-center"}>{error}</div>
                )}
                {results.length > 0 && (
                    <div className={"mt-4 bg-white border border-gray-200 rounded-lg shadow-lg max-h-96 overflow-y-auto"}>
                        {results.map((song) => (
                            <div
                             key={song.id}
                            className={"p-4 border-b border-gray-100 hover:bg-gray-50 cursor-pointer transition-colors duration-150 last:border-b-0"}
                            onClick={() => handleSongSelect(song.id)}
                            >
                                <div className={"font-semibold text-gray-900 truncate"}>{song.title}</div>
                                <div className={"text-gray-600 text-sm mt-1"}>{song.artist}</div>
                    </div>
                ))}
            </div>
            )}
        </div>
    );
};
