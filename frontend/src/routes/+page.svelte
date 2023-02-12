<script>

    import {BreadcrumbItem, Heading} from "flowbite-svelte";
    import Container from "../components/Container.svelte";
    import ResponsiveBreadCrumb from "../components/ResponsiveBreadCrumb.svelte";
    import {onMount} from "svelte";
    import Requests from "../components/Requests.svelte";
    import Request from "../components/Request.svelte";

    export let error;

    let requests = null;
    let entries = null;
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

    function loadEntries() {
        entries = null;
        fetch("/api/entries")
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    entries = resp.content;
                }
            })
            .catch(err => console.log(err))
    }

    function findEntry(entry_id) {
        if (entries != null) {
            return entries.filter(entry => entry.id === entry_id);
        }
        console.log("No entries found.")

    }


    onMount(() => {
        loadEntries()
        loadRequests()
        setTimeout(() => {
        }, 250);

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
                        request={pr.first}
                        entry={findEntry(pr.first.entry_id)}
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
                        entry={findEntry(ar.first.entry_id)}
                        accepted=true
                />
            {/each}
        </Requests>
    </div>

</Container>
