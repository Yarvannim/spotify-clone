import type {SongSearchResponse} from "../types/song.ts";

class ApiService {

    async getStreamUrl(songId: string): Promise<string> {
        const response = await fetch(`/api/stream/${songId}`);
        console.log(response)
        if (!response.ok) {
            throw new Error('Failed to fetch stream URL');
        }

        return await response.text();
    }

    async searchSongs(query: string): Promise<SongSearchResponse[]>{
        if (!query.trim()) return [];

        const response = await fetch(`/api/search?q=${encodeURIComponent(query)}`,
            {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            }
        );
        if (!response.ok) {
            throw new Error('Failed to fetch search results');
        }
        return await response.json();
    }
}

export const apiService = new ApiService();