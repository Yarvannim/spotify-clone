import { useState, useCallback } from 'react';
import type {SongSearchResponse} from "../types/song.ts";
import { apiService} from "../services/api.ts";

export const useSongSearch = () => {
    const [results, setResults] = useState<SongSearchResponse[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<String | null>(null);

    const search = useCallback(async (query: string) => {
        if (!query.trim()) {
            setResults([]);
            setError(null);
            return;
        }

        setIsLoading(true)
        setError(null)
        setResults([])

        try {
            const searchResults = await apiService.searchSongs(query)
            setResults(searchResults)
        }catch (error){
            setError('Search failed. Please try again.')
            console.error('Error searching songs:', error)
        } finally {
            setIsLoading(false)
        }
    }, [])

    const clearResults = useCallback(() =>  {
        setResults([]);
        setError(null);
    }, [])

    return {
        results,
        isLoading,
        error,
        search,
        clearResults,
    }
}