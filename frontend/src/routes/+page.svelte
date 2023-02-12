<script>

    import {BreadcrumbItem, Heading} from "flowbite-svelte";
    import Container from "../components/Container.svelte";
    import ResponsiveBreadCrumb from "../components/ResponsiveBreadCrumb.svelte";
    import {onMount} from "svelte";
    import Requests from "../components/Requests.svelte";
    import Request from "../components/Request.svelte";

    export let error;

    let requests = null;
    let pending_requests = [];
    let accepted_requests = [];

    function loadRequests() {
        requests = null;

        fetch("/api/requests")
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    requests = resp.content;
                    filterRequests();
                }
            })
            .catch(err => console.log(err))
    }


    function filterRequests() {
        if (requests !== null) {
            pending_requests = requests.filter(request => request.first.state === "PENDING")
            console.log(pending_requests);
            accepted_requests = requests.filter(request => request.first.state === "ACCEPTED")
        } else {
            console.log("No requests found.")
        }
    }

    onMount(() => {
        loadRequests()
    });

</script>

<Container>
    <ResponsiveBreadCrumb>
        <BreadcrumbItem home href="/">Home</BreadcrumbItem>
    </ResponsiveBreadCrumb>
    <Heading class="mb-4" tag="h2">Home</Heading>


    <div class="justify-center flex w-full gap-x-4">
        <Requests
                pending=true
        >
            {#each pending_requests as pr}
                <Request
                        on:requestUpdated={() => loadRequests()}
                        request={pr.first}
                        entry={pr.second}
                        pending=true
                />
            {/each}
        </Requests>
        <Requests
                accepted=true
        >
            {#each accepted_requests as ar}
                <Request
                        request={ar.first}
                        entry={ar.second}
                        accepted=true
                />
            {/each}
        </Requests>
    </div>

</Container>
