<script>
  import PdfViewer from "./PdfViewer.svelte";
  import {Button} from "flowbite-svelte";

  export let document;


</script>

<style>
  #pdfContainer {
    @apply w-[100%] h-full drop-shadow-lg border-4 border-pdf-bg bg-pdf-bg rounded-xl overflow-hidden;
  }

  #pdfContainer.hidden {
    @apply w-0 h-0;
  }
</style>

<div class="flex">
  <a class="mx-auto my-4" href="/lorem_ipsum.pdf" target="_blank">
    <Button class="md:hidden" outline>
      <span class="px-10">View Paper</span>
    </Button>
  </a>
</div>


<div class="max-md:hidden" id="pdfContainer">
  <PdfViewer document={document} />
</div>
