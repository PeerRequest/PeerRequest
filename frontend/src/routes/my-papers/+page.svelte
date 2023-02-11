<script>
    import Container from "../../components/Container.svelte";
    import mock_data from "../../mock_data.js";
    import {BreadcrumbItem, Heading} from "flowbite-svelte";
    import ResponsiveBreadCrumb from "../../components/ResponsiveBreadCrumb.svelte";
    import Papers from "../../components/Papers.svelte";
    import Paper from "../../components/Paper.svelte";
    import {onMount} from "svelte";

    const user = mock_data.users[23];
    const papers = mock_data.papers;

    export let error;
    let entries = null;

    function loadUserEntries() {
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

    onMount(() => {
        loadUserEntries();
    });
</script>

<svelte:head>
    <title>My Papers | PeerRequest</title>
</svelte:head>

<Container>
    <ResponsiveBreadCrumb>
        <BreadcrumbItem home href="/">Home</BreadcrumbItem>
        <BreadcrumbItem href="/my-papers">My Papers</BreadcrumbItem>
    </ResponsiveBreadCrumb>
    <Heading class="mb-4" tag="h2">My Papers</Heading>

    <Papers show_category=true>

        {#each entries !== null as p}
            <Paper
                    href="/categories/{p.category.id}/{p.id}"
                    paper={p}
                    category={p.category}
            />
        {/each}
    </Papers>


</Container>