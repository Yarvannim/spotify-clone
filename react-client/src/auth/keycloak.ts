import Keycloak from 'keycloak-js';

const keycloakConfig = {
    url: 'http://keycloak:8080',
    realm: 'spotify-clone',
    clientId: 'spotify-frontend'
};

export const keycloak = new Keycloak(keycloakConfig);

export const initKeycloak = (): Promise<boolean> => {
    return  keycloak.init({
        onLoad: 'check-sso',
        pkceMethod: 'S256',
        checkLoginIframe: true,
    });
};
