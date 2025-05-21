import GlobitsBreadcrumb from "../../../common/GlobitsBreadcrumb";
import { Grid } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import EvaluationTicketTable from "./EvaluationTicketTable";
import { useEffect } from "react";
import { useStore } from "app/stores";
import EvaluationTicketFilter from "./EvaluationTicketFilter";
import { useLocation } from "react-router-dom";

export default function EvaluationTicketIndex() {
    const {t} = useTranslation();
    const {resetStore , pageEvaluationForms , search} = useStore().evaluationTicketStore
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);

    useEffect(() => {
        pageEvaluationForms(search)

        return resetStore
    } , []);
    return (
        <>
            <section className='staff-root flex-column'>
                <div className='px-25 bg-white'>
                    <GlobitsBreadcrumb
                        routeSegments={[{name:t("staff.title")} , {name:t("navigation.staff.evaluationForm")}]}
                    />
                </div>
                <Grid container spacing={2} className='p-12 h-100 pb-48'>
                    <Grid
                        item
                        xs={12}
                        className='h-100'>
                        <div className='bg-white p-12 h-100 overflow-auto'>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <EvaluationTicketFilter/>
                                </Grid>
                                <Grid item xs={12}>
                                    <EvaluationTicketTable/>
                                </Grid>
                            </Grid>
                        </div>
                    </Grid>
                </Grid>
            </section>
        </>
    )
}