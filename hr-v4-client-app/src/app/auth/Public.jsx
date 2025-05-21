import React, {Fragment, useEffect, useState} from 'react'
import {renderRoutes} from "react-router-config";
import AppContext from "../appContext";

export default function Public() {
    const routesComponent = (routes) => {
        let newRoutes = [];
        routes.map((item, key) => {
            newRoutes.push(item);
        });

        return renderRoutes(newRoutes);
    };

    useEffect(() => {
    }, [])
    return <>
        <div style={{ height: '100vh', overflowY: 'auto' }}>
            <AppContext.Consumer>
                {({ routes }) => (
                    <>
                        {routesComponent(routes)}
                    </>
                )}
            </AppContext.Consumer>
        </div>
    </>;
}
