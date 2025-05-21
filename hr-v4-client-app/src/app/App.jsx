import "../styles/_app.scss";
import React, { useEffect } from "react";
import { Provider } from "react-redux";
import { Router } from "react-router-dom";
import EgretTheme from "./EgretLayout/EgretTheme/EgretTheme";
import AppContext from "./appContext";
import history from "history.js";

import routes from "./RootRoutes";
import { Store } from "./redux/Store";
import Auth from "./auth/Auth";
import EgretLayout from "./EgretLayout/EgretLayout";
import AuthGuard from "./auth/AuthGuard";
import "../styles/nprogress.css";
import { loadProgressBar } from "axios-progress-bar";
import { observer } from "mobx-react";
import './tailwind-config.js';
import Public from "./auth/Public";
import { ToastContainer } from "react-toastify";

loadProgressBar ();
const App = () => {
  const routesPath = window.location.pathname;
  useEffect (() => {
    if (window.location.hash.includes ('iss=')) {
      const cleanUrl = window.location.href.split ('#')[0];
      window.history.replaceState (null, '', cleanUrl);
    }
  }, []);
  // console.log("hello")
  return (
      <AppContext.Provider value={{routes}}>
        <Provider store={Store}>
          <EgretTheme>
            <>
              {routesPath.includes ("public-router")? (
                  <>
                    <Router history={history}>
                      <Public/>
                    </Router>
                  </>
              ) : (
                  <>
                    <Auth>
                      <Router history={history}>
                        <AuthGuard>
                          <EgretLayout/>
                        </AuthGuard>
                      </Router>
                    </Auth>
                  </>
              )}

              <ToastContainer position="bottom-right"/>
            </>
          </EgretTheme>
        </Provider>
      </AppContext.Provider>
  );
};

export default observer (App);
