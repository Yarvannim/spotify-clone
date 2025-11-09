import type {SongSearchResponse} from "../types/song.ts";
import {keycloak} from "../auth/keycloak.ts";

class ApiService {
    private async getAuthHeaders(): Promise<HeadersInit>{
        const headers: HeadersInit = {
            'Content-Type': 'application/json',
        };

        if (keycloak.authenticated) {
            try {
                await keycloak.updateToken(30);
            } catch (error) {
                keycloak.login();
                throw new Error('Authentication required');
            }
            if (keycloak.token){
                headers['Authorization'] = `Bearer ${keycloak.token}`;
            }
        }
        return headers;
    }

    async getStreamUrl(songId: string): Promise<string> {
        const headers = await this.getAuthHeaders();
        const response = await fetch(`/api/stream/${songId}`, {
            headers
        });
        console.log(response)
        if (!response.ok) {
            if (response.status === 401) {
                throw new Error('Authentication is required to stream music');
            }
            throw new Error('Failed to fetch stream URL');
        }
        return await response.text();
    }

    async searchSongs(query: string): Promise<SongSearchResponse[]>{
        if (!query.trim()) return [];

        const headers = await this.getAuthHeaders();

        const response = await fetch(`/api/search?q=${encodeURIComponent(query)}`,
            {
                method: 'GET',
                headers
            }
        );
        if (!response.ok) {
            throw new Error('Failed to fetch search results');
        }
        return await response.json();
    }
}

export const apiService = new ApiService();