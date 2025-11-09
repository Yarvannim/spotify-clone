import { keycloak } from "../auth/keycloak.ts";

export interface UserProfile {
    userId: string;
    username: string;
    displayName: string;
    isArtist: boolean;
    createdAt: string;
}

class UserApiService {
    private async getAuthHeaders(): Promise<HeadersInit>{
        const headers: HeadersInit = {
            'Content-Type': 'application/json',
        };

        if (keycloak.authenticated && keycloak.token){
            headers['Authorization'] = `Bearer ${keycloak.token}`;
        }
        return headers;
    }

    async syncUser(): Promise<UserProfile> {
        const headers = await this.getAuthHeaders();
        const response = await fetch('/api/users/sync', {
            method: 'POST',
            headers
        });
        if (!response.ok) {
            throw new Error('Failed to fetch user profile');
        }
        return await response.json();
    }

    async getCurrentUser(): Promise<UserProfile> {
        const headers = await this.getAuthHeaders();
        const response = await fetch('/api/users/me', {
            headers
        });
        if (!response.ok) {
            throw new Error('Failed to fetch user profile');
        }

        return await response.json();
    }

    async updateDisplayName(displayName: string): Promise<UserProfile> {
        const headers = await this.getAuthHeaders();
        const response = await fetch('/api/users/me/display-name', {
            method: 'PUT',
            headers,
            body: JSON.stringify({ displayName })
        });

        if (!response.ok) {
            throw new Error('Failed to update display name');
        }
        return await response.json();
    }

    async checkIsArtist(): Promise<boolean> {
        const headers = await this.getAuthHeaders();
        const response = await fetch('/api/users/me/is-artist', {
            headers
        });

        if (!response.ok) {
            return false;
        }

        return await response.json();
    }
}

export const userApiService = new UserApiService();