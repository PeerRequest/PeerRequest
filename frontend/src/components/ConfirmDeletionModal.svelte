<script>
    import {
        Modal,
        CloseButton,
        Button

    } from "flowbite-svelte" ;
    import {goto} from "$app/navigation";

    export let show = false;
    export let to_delete;
    export let delete_name;
    export let afterpath = null;
    export let hide = () => {
        /* NOP */
    };
    export let result = () => {
        /* NOP */
    }

    function deleteObject() {
        fetch("/api" + to_delete, {
            method: 'DELETE',
        })
            .then((response) => console.log(response))
            .then((response_data) => {
                if (afterpath !==  null) {
                    goto(afterpath)
                }
                hide()
            })
            .catch(err => console.log(err))

    }
</script>

<Modal bind:open={show} on:hide={() => hide ? hide() : null} size="sm" permanent={true}>
    <div class="text-2xl font-extrabold text-gray-900 text-center">
        Confirm Deletion of
        <br>
        {delete_name}
    </div>
    <CloseButton class="absolute top-0 right-2"
                 on:click={() => hide()}/>
    <div class="text-center font-bold">
        Are you sure?
    </div>
    <div class="justify-center gap-x-16 w-full flex">
        <Button aria-label="Confirm deletion" class="mb-4 h-8" color="primary" on:click={() => deleteObject()} size="lg">Yes</Button>
        <Button aria-label="No deletion" class="mb-4 h-8" color="red" on:click={() => hide()} size="lg">No</Button>
    </div>

</Modal>